package com.africom.correspmanage.service;

import com.africom.correspmanage.client.ConfigManageRestClient;
import com.africom.correspmanage.client.ModelNotifDTO;
import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.ResponsableTache;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.repository.TasksRepository;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * Construit, pour chaque responsable, un mail de synthèse listant ses tâches IN_PROGRESS
 * dont l'échéance est dans <= windowHours ou déjà dépassée (corresp non SUPPRIMER).
 * Le HTML est rendu via FreeMarker (ModelNotif RAPPEL_ECHEANCES_DIGEST de ConfigManage).
 */
@Service
public class TaskEcheanceDigestService {

    private final Logger log = LoggerFactory.getLogger(TaskEcheanceDigestService.class);

    private static final String MODEL_CODE = "RAPPEL_ECHEANCES_DIGEST";
    private static final String DEFAULT_SENDER = "noreply@africomservices.com";
    private static final String SENDER_DISPLAY_NAME = "CorrespCare";
    private static final String DEFAULT_SUBJECT = "[Rappel] ${nbTaches} tâche(s) à échéance proche ou dépassée";
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TasksRepository tasksRepository;
    private final ResponsableTacheService responsableTacheService;
    private final OrgaCareRestClient orgaCareRestClient;
    private final ConfigManageRestClient configManageRestClient;
    private final TemplateRenderService templateRenderService;
    private final String frontendUrl;

    public TaskEcheanceDigestService(
        TasksRepository tasksRepository,
        ResponsableTacheService responsableTacheService,
        OrgaCareRestClient orgaCareRestClient,
        ConfigManageRestClient configManageRestClient,
        TemplateRenderService templateRenderService,
        ApplicationProperties applicationProperties
    ) {
        this.tasksRepository = tasksRepository;
        this.responsableTacheService = responsableTacheService;
        this.orgaCareRestClient = orgaCareRestClient;
        this.configManageRestClient = configManageRestClient;
        this.templateRenderService = templateRenderService;
        this.frontendUrl = applicationProperties.getFrontendUrl();
    }

    private static class Resp {
        String email;
        String name;
        String ccEmail; // email du département parent (N+1), null si absent
    }

    private static class Group {
        String email;
        String name;
        List<Map<String, Object>> taches = new ArrayList<>();
        Set<Long> taskIds = new HashSet<>();
        Set<String> ccEmails = new LinkedHashSet<>(); // N+1 de tous les départements regroupés sous cette adresse
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildDigestMails(int windowHours, String overrideEmail) {
        ModelNotifDTO model = loadModel();

        ZonedDateTime now = ZonedDateTime.now();
        List<Tasks> tasks = new ArrayList<>(
            tasksRepository.findInProgressDueBefore(CorrespStatus.IN_PROGRESS, now.plusHours(windowHours))
        );
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        tasks.sort(Comparator.comparing(Tasks::getDueDate)); // dépassées d'abord, puis les plus proches

        // Responsables de toutes les tâches en une requête
        List<Long> ids = tasks.stream().map(Tasks::getId).collect(Collectors.toList());
        List<ResponsableTache> allResp = responsableTacheService.findByTacheIdIn(ids);
        if (allResp == null) {
            allResp = Collections.emptyList();
        }
        Map<Long, List<ResponsableTache>> respByTask = allResp
            .stream()
            .filter(r -> r.getTacheId() != null)
            .collect(Collectors.groupingBy(ResponsableTache::getTacheId));

        // Regroupement par adresse email du responsable
        Map<String, Resp> respCache = new HashMap<>();
        Map<String, Group> groups = new LinkedHashMap<>();

        for (Tasks t : tasks) {
            Corresp c = pickCorresp(t);
            if (c == null) {
                continue;
            }
            Map<String, Object> item = buildItem(t, c, now);

            for (ResponsableTache r : respByTask.getOrDefault(t.getId(), Collections.emptyList())) {
                String key =
                    (r.getCodeDept() == null ? "" : r.getCodeDept().trim()) +
                        "|" +
                        (r.getMatriculePers() == null ? "" : r.getMatriculePers().trim().toLowerCase());
                if ("|".equals(key)) {
                    continue;
                }
                Resp resp = respCache.computeIfAbsent(key, k -> resolveResponsable(r));
                if (resp.email == null) {
                    continue;
                }
                Group g = groups.computeIfAbsent(
                    resp.email.toLowerCase(),
                    k -> {
                        Group ng = new Group();
                        ng.email = resp.email;
                        ng.name = resp.name;
                        return ng;
                    }
                );
                if (g.taskIds.add(t.getId())) {
                    g.taches.add(item);
                }
                if (resp.ccEmail != null) {
                    g.ccEmails.add(resp.ccEmail);
                }
            }
        }

        // Rendu FreeMarker : un mail par responsable
        String sender = resolveSender(model);
        String subjectTemplate = (model.getSubjectFormula() != null && !model.getSubjectFormula().isBlank())
            ? model.getSubjectFormula()
            : DEFAULT_SUBJECT;
        boolean override = overrideEmail != null && !overrideEmail.isBlank();

        List<Map<String, Object>> mails = new ArrayList<>();
        for (Group g : groups.values()) {
            try {
                long nbDep = g.taches.stream().filter(m -> Boolean.TRUE.equals(m.get("depassee"))).count();

                Map<String, Object> ctx = new HashMap<>();
                ctx.put("nomDestinataire", esc(g.name));
                ctx.put("emailDestinataire", g.email);
                ctx.put("taches", g.taches);
                ctx.put("nbTaches", g.taches.size());
                ctx.put("nbDepassees", nbDep);
                ctx.put("nbProches", g.taches.size() - nbDep);
                ctx.put("dateAujourdhui", now.format(DATE_FMT));
                ctx.put("lienApplication", frontendUrl);

                String subject = templateRenderService.render(subjectTemplate, ctx);
                String html = templateRenderService.render(model.getContent(), ctx);

                // CC = email du département parent (N+1), jamais identique au destinataire principal
                String cc = g.ccEmails
                    .stream()
                    .filter(c -> !c.equalsIgnoreCase(g.email))
                    .collect(Collectors.joining(","));

                Map<String, Object> mail = new LinkedHashMap<>();
                mail.put("email", override ? overrideEmail.trim() : g.email);
                mail.put("originalEmail", g.email);
                // En mode test (overrideEmail), le CC n'est PAS envoyé aux vrais N+1 : seul "originalCc" l'indique
                mail.put("cc", override ? "" : cc);
                mail.put("originalCc", cc);
                mail.put("nomDestinataire", g.name);
                mail.put("sender", sender);
                mail.put(
                    "subject",
                    override ? "[TEST pour " + g.email + (cc.isEmpty() ? "" : " | CC prévu : " + cc) + "] " + subject : subject
                );
                mail.put("html", html);
                mail.put("nbTaches", g.taches.size());
                mail.put("nbDepassees", nbDep);
                mails.add(mail);
            } catch (Exception e) {
                log.error("Rendu du mail de rappel impossible pour {} : {}", g.email, e.getMessage(), e);
            }
        }

        log.info("Rappels d'échéance : {} tâche(s), {} responsable(s), {} mail(s) prêt(s)", tasks.size(), groups.size(), mails.size());
        return mails;
    }

    // ------------------------------------------------------------------ helpers

    private ModelNotifDTO loadModel() {
        ModelNotifDTO model;
        try {
            model = configManageRestClient.getModelNotifByCode(MODEL_CODE);
        } catch (Exception e) {
            throw new IllegalStateException("ModelNotif '" + MODEL_CODE + "' introuvable : " + e.getMessage(), e);
        }
        if (model == null || model.getContent() == null || model.getContent().isBlank()) {
            throw new IllegalStateException("ModelNotif '" + MODEL_CODE + "' vide ou inexistant");
        }
        return model;
    }

    private String resolveSender(ModelNotifDTO model) {
        String s = model.getSenderEmailFormule();
        String address = (s == null || s.isBlank() || s.contains("$")) ? DEFAULT_SENDER : s.trim();

        // Déjà au format "Nom <email>" : on ne touche à rien
        if (address.contains("<") && address.contains(">")) {
            return address;
        }
        return SENDER_DISPLAY_NAME + " <" + address + ">";
    }

    private Corresp pickCorresp(Tasks t) {
        return t
            .getCorresps()
            .stream()
            .filter(c -> c.getClassStatus() == null || !"SUPPRIMER".equals(c.getClassStatus()))
            .findFirst()
            .orElse(null);
    }

    private Map<String, Object> buildItem(Tasks t, Corresp c, ZonedDateTime now) {
        ZonedDateTime due = t.getDueDate();
        boolean overdue = due.isBefore(now);
        String delai = overdue
            ? "Dépassée depuis " + formatDuration(Duration.between(due, now))
            : "Il reste " + formatDuration(Duration.between(now, due));

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", t.getId());
        m.put("reference", c.getReference() != null ? esc(c.getReference()) : "-");
        m.put("objetCorresp", c.getSubject() != null ? esc(c.getSubject()) : "-");
        m.put("typeTask", t.getTaskType() != null ? esc(t.getTaskType()) : "-");
        m.put("dateLimite", due.withZoneSameInstant(ZoneId.systemDefault()).format(DATE_TIME_FMT));
        m.put("depassee", overdue);
        m.put("delai", delai);
        m.put("lien", frontendUrl + "/corresp/" + c.getId() + "/view?taskId=" + t.getId());
        return m;
    }

    private Resp resolveResponsable(ResponsableTache r) {
        Resp resp = new Resp();
        String dept = r.getCodeDept() != null ? r.getCodeDept().trim() : "";
        String mat = r.getMatriculePers() != null ? r.getMatriculePers().trim() : "";
        try {
            if (!dept.isEmpty()) {
                resp.email = extractEmail(orgaCareRestClient.getDepartementDtoByCode(dept));
                if (resp.email != null) {
                    try {
                        resp.name = orgaCareRestClient.getDepartementNomByCode(dept);
                    } catch (Exception e) {
                        resp.name = dept;
                    }
                    resp.ccEmail = resolveParentDepartementEmail(dept);
                }
            }
            if (resp.email == null && !mat.isEmpty()) {
                resp.email = extractEmail(orgaCareRestClient.getPersonneByMatricule(mat));
                if (resp.email != null) {
                    try {
                        Map<String, String> names = orgaCareRestClient.getPersonneNamesByMatricules(List.of(mat));
                        resp.name = names != null ? names.getOrDefault(mat, names.getOrDefault(mat.toLowerCase(), mat)) : mat;
                    } catch (Exception e) {
                        resp.name = mat;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Résolution du responsable (dept='{}', matricule='{}') en échec : {}", dept, mat, e.getMessage());
        }
        if (resp.email == null) {
            log.warn("Aucun email pour le responsable dept='{}' matricule='{}' : tâches ignorées pour ce responsable", dept, mat);
        }
        if (resp.name == null || resp.name.isBlank()) {
            resp.name = !dept.isEmpty() ? dept : mat;
        }
        return resp;
    }

    /**
     * Email du département parent (N+1). Retourne null s'il n'y a pas de parent ou pas d'email.
     * Ne bloque jamais l'envoi du mail principal en cas d'erreur.
     */
    private String resolveParentDepartementEmail(String deptCode) {
        try {
            String cc = extractEmail(orgaCareRestClient.getParentDepartementByCode(deptCode));
            if (cc == null) {
                log.debug("Pas d'email N+1 pour le département '{}'", deptCode);
            }
            return cc;
        } catch (Exception e) {
            log.warn("Résolution du département parent (N+1) de '{}' en échec : {}", deptCode, e.getMessage());
            return null;
        }
    }

    private String extractEmail(Map<String, Object> dto) {
        if (dto == null) {
            return null;
        }
        Object e = dto.get("email");
        return (e == null || e.toString().isBlank()) ? null : e.toString().trim();
    }

    private String formatDuration(Duration d) {
        long minutes = Math.max(0, d.toMinutes());
        long days = minutes / (60 * 24);
        long hours = (minutes % (60 * 24)) / 60;
        long mins = minutes % 60;
        if (days > 0) return hours > 0 ? days + " j " + hours + " h" : days + " j";
        if (hours > 0) return mins > 0 ? hours + " h " + mins + " min" : hours + " h";
        if (mins > 0) return mins + " min";
        return "quelques instants";
    }

    /** Échappe les valeurs insérées dans le HTML (les < > & de l'objet ne cassent pas le mail). */
    private static String esc(String s) {
        return s == null ? "" : HtmlUtils.htmlEscape(s, "UTF-8");
    }
}
