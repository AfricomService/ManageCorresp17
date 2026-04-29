package com.africom.correspmanage.service;

import com.africom.correspmanage.client.ConfigManageRestClient;
import com.africom.correspmanage.client.ModelNotifDTO;
import com.africom.correspmanage.client.OrgaCareRestClient;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.domain.Tasks;
import com.africom.correspmanage.service.dto.ResponsableTacheDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.africom.correspmanage.config.ApplicationProperties;

@Service
public class TaskNotifService {

    private final Logger log = LoggerFactory.getLogger(TaskNotifService.class);

    private final ConfigManageRestClient configManageRestClient;
    private final TemplateRenderService templateRenderService;
    private final EmailSendService emailSendService;
    private final OrgaCareRestClient orgaCareRestClient;
    private final String frontendUrl;

    private static final String MODEL_CODE = "NOTIF_TACHE";

    public TaskNotifService(
        ConfigManageRestClient configManageRestClient,
        TemplateRenderService templateRenderService,
        EmailSendService emailSendService,
        OrgaCareRestClient orgaCareRestClient,
        ApplicationProperties applicationProperties
    ) {
        this.configManageRestClient = configManageRestClient;
        this.templateRenderService = templateRenderService;
        this.emailSendService = emailSendService;
        this.orgaCareRestClient = orgaCareRestClient;
        this.frontendUrl = applicationProperties.getFrontendUrl();
    }

    public void notifyTaskResponsables(Tasks task, Corresp corresp, List<ResponsableTacheDTO> responsables) {
        if (responsables == null || responsables.isEmpty()) return;

        ModelNotifDTO modelNotif;
        try {
            modelNotif = configManageRestClient.getModelNotifByCode(MODEL_CODE);
        } catch (Exception e) {
            log.warn("ModelNotif '{}' introuvable : {}", MODEL_CODE, e.getMessage());
            return;
        }
        if (modelNotif == null) return;

        for (ResponsableTacheDTO responsable : responsables) {
            try {
                // TO : toujours l'email du département/personne (comportement inchangé)
                String emailDestinataire = resolveEmail(responsable);
                String nomDestinataire   = resolveName(responsable);

                if (emailDestinataire == null || emailDestinataire.isBlank()) {
                    log.debug("Pas d'email TO pour responsable {}, notification ignorée", responsable);
                    continue;
                }

                Long departementId = resolveDepartementId(responsable);

                // CC : résolu depuis ccEmailFormule du ModelNotif
                List<Map<String, String>> ccList = resolveCcFromFormule(
                    modelNotif.getCcEmailFormule(),
                    departementId,
                    emailDestinataire
                );

                Map<String, Object> ctx = buildContext(task, corresp, nomDestinataire,
                    emailDestinataire, departementId);

                String renderedHtml = templateRenderService.render(modelNotif.getContent(), ctx);
                String renderedSubject = templateRenderService.render(
                    modelNotif.getSubjectFormula() != null
                        ? modelNotif.getSubjectFormula() : "Nouvelle tâche assignée",
                    ctx
                );

                Message message = new Message();
                message.setSender(modelNotif.getSenderEmailFormule());
                message.setReceiver(List.of(Map.of("email", emailDestinataire)));
                message.setCopyTo(ccList);
                message.setCorrespId(corresp != null ? corresp.getId() : null);
                message.setMessageType("EMAIL_NOTIF_TACHE");

                emailSendService.sendEmailDirectWithHtml(message, renderedHtml, renderedSubject);

                log.info("Notification envoyée → TO:{} CC:{} (tâche id={})",
                    emailDestinataire, ccList.size(), task.getId());

            } catch (Exception e) {
                log.error("Erreur notification pour responsable {} : {}", responsable, e.getMessage(), e);
            }
        }
    }

    /**
     * Résout l'email TO depuis recepientEmailFormule.
     * - Si la formule est "${emailDestinataire-TYPE}" → récupère l'email du TYPE dans le département
     * - Si la formule est "${emailDestinataire}" ou null → utilise l'email du département/personne (ancien comportement)
     */
    private String resolveEmailFromFormule(String formule, ResponsableTacheDTO responsable, Long departementId) {

        // Cas : formule contient ${emailDestinataire-TYPE}
        if (formule != null && formule.matches(".*\\$\\{emailDestinataire-([A-Z_]+)\\}.*")) {
            java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\\$\\{emailDestinataire-([A-Z_]+)\\}")
                .matcher(formule);
            if (m.find() && departementId != null) {
                String type = m.group(1); // ex: "MEMBRE", "CHEF", "ASSISTANT", "INTERIM"
                try {
                    List<String> emails = orgaCareRestClient
                        .getEmailsByDepartementAndType(departementId, type);
                    if (emails != null && !emails.isEmpty()) {
                        // Si plusieurs emails du même type → on prend le 1er
                        // (ou on pourrait envoyer à tous — voir note ci-dessous)
                        return emails.get(0);
                    }
                } catch (Exception e) {
                    log.warn("Impossible de résoudre emailDestinataire-{} pour deptId={} : {}",
                        type, departementId, e.getMessage());
                }
            }
            return null;
        }

        // Cas : formule est "${emailDestinataire}" ou null → ancien comportement
        return resolveEmail(responsable);
    }

    /**
     * Résout la liste CC depuis ccEmailFormule du ModelNotif.
     * Supporte les patterns ${emailDestinataire-TYPE} et les emails fixes.
     * Exemple de ccEmailFormule : [{"email": "${emailDestinataire-CHEF}"}]
     */
    private List<Map<String, String>> resolveCcFromFormule(
        List<Map<String, String>> ccEmailFormule,
        Long departementId,
        String emailTo) {

        List<Map<String, String>> ccList = new ArrayList<>();
        if (ccEmailFormule == null || ccEmailFormule.isEmpty()) return ccList;

        java.util.regex.Pattern pattern = java.util.regex.Pattern
            .compile("\\$\\{emailDestinataire-([A-Z_]+)\\}");

        for (Map<String, String> entry : ccEmailFormule) {
            String emailValue = entry.get("email");
            if (emailValue == null || emailValue.isBlank()) continue;

            java.util.regex.Matcher m = pattern.matcher(emailValue.trim());
            if (m.matches()) {
                // Pattern dynamique → résoudre via OrgaCare
                String type = m.group(1); // CHEF, MEMBRE, ASSISTANT, INTERIM
                if (departementId != null) {
                    try {
                        List<String> emails = orgaCareRestClient
                            .getEmailsByDepartementAndType(departementId, type);
                        if (emails != null) {
                            emails.stream()
                                .filter(e -> e != null && !e.isBlank()
                                    && !e.equalsIgnoreCase(emailTo))
                                .forEach(e -> ccList.add(Map.of("email", e)));
                        }
                    } catch (Exception e) {
                        log.warn("Impossible de résoudre CC emailDestinataire-{} pour deptId={} : {}",
                            type, departementId, e.getMessage());
                    }
                }
            } else {
                // Email fixe → ajouter directement
                if (!emailValue.trim().equalsIgnoreCase(emailTo)) {
                    ccList.add(Map.of("email", emailValue.trim()));
                }
            }
        }
        return ccList;
    }

    private String resolveEmail(ResponsableTacheDTO responsable) {
        // Cas 1 : responsable est un département → récupérer l'email du département
        if (responsable.getCodeDept() != null && !responsable.getCodeDept().isBlank()) {
            try {
                Map<String, Object> deptDto = orgaCareRestClient.getDepartementDtoByCode(responsable.getCodeDept());
                if (deptDto != null) {
                    Object email = deptDto.get("email");
                    if (email != null && !email.toString().isBlank()) {
                        log.debug("Email département trouvé pour {} : {}", responsable.getCodeDept(), email);
                        return email.toString().trim();
                    } else {
                        log.debug("Pas d'email configuré pour le département {}", responsable.getCodeDept());
                    }
                }
            } catch (Exception e) {
                log.warn("Impossible de récupérer l'email du département {} : {}", responsable.getCodeDept(), e.getMessage());
            }
        }

        // Cas 2 : responsable est une personne (matricule) → chercher son email via OrgaCare
        if (responsable.getMatriculePers() != null && !responsable.getMatriculePers().isBlank()) {
            try {
                // Appel vers OrgaCare pour récupérer les infos de la personne
                Map<String, Object> personneDto = orgaCareRestClient.getPersonneByMatricule(responsable.getMatriculePers());
                if (personneDto != null) {
                    Object email = personneDto.get("email");
                    if (email != null && !email.toString().isBlank()) {
                        log.debug("Email personne trouvé pour {} : {}", responsable.getMatriculePers(), email);
                        return email.toString().trim();
                    } else {
                        log.debug("Pas d'email configuré pour la personne {}", responsable.getMatriculePers());
                    }
                }
            } catch (Exception e) {
                log.warn("Impossible de récupérer l'email de la personne {} : {}", responsable.getMatriculePers(), e.getMessage());
            }
        }

        return null;
    }

    private String resolveName(ResponsableTacheDTO responsable) {
        if (responsable.getCodeDept() != null && !responsable.getCodeDept().isBlank()) {
            try {
                return orgaCareRestClient.getDepartementNomByCode(responsable.getCodeDept());
            } catch (Exception e) {
                return responsable.getCodeDept();
            }
        }
        if (responsable.getMatriculePers() != null && !responsable.getMatriculePers().isBlank()) {
            try {
                Map<String, String> names = orgaCareRestClient.getPersonneNamesByMatricules(
                    List.of(responsable.getMatriculePers())
                );
                return names.getOrDefault(responsable.getMatriculePers(), responsable.getMatriculePers());
            } catch (Exception e) {
                return responsable.getMatriculePers();
            }
        }
        return "Responsable";
    }

    // Dans TaskNotifService.java — AJOUTER cette méthode

    private Long resolveDepartementId(ResponsableTacheDTO responsable) {
        if (responsable.getCodeDept() == null || responsable.getCodeDept().isBlank()) {
            return null;
        }
        try {
            Map<String, Object> deptDto = orgaCareRestClient
                .getDepartementDtoByCode(responsable.getCodeDept());
            if (deptDto != null && deptDto.get("id") != null) {
                return Long.valueOf(deptDto.get("id").toString());
            }
        } catch (Exception e) {
            log.warn("Impossible de résoudre le departementId pour {} : {}",
                responsable.getCodeDept(), e.getMessage());
        }
        return null;
    }

    private Map<String, Object> buildContext(Tasks task, Corresp corresp,
                                             String nomDestinataire, String emailDestinataire, Long departementId) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("typeTask",          task.getTaskType() != null ? task.getTaskType() : "");
        String typeTask = task.getTaskType() != null ? task.getTaskType().toUpperCase() : "N/A";
        ctx.put("objetTache",        nomDestinataire + " effectuera la tâche de " + typeTask);
        ctx.put("nomDestinataire",   nomDestinataire);
        ctx.put("emailDestinataire", emailDestinataire);
        ctx.put("commentaire",       task.getCommentaire() != null ? task.getCommentaire() : "");
        ctx.put("dateLimite",        task.getDueDate() != null
            ? task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        ctx.put("dateAujourdhui",    ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // ← NOUVEAU : permet à TemplateRenderService de résoudre ${emailDestinataire-CHEF} etc.
        if (departementId != null) {
            ctx.put("departementId", departementId);
        }

        if (corresp != null) {
            ctx.put("referenceCorresp", corresp.getReference() != null ? corresp.getReference() : "");
            ctx.put("objetCorresp",     corresp.getSubject() != null ? corresp.getSubject() : "");
            ctx.put("lienTache", frontendUrl + "/corresp/" + corresp.getId() + "/view?taskId=" + task.getId());
        } else {
            ctx.put("referenceCorresp", "");
            ctx.put("objetCorresp",     "");
            ctx.put("lienTache", frontendUrl);
        }

        return ctx;
    }
}
