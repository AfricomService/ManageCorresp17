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
        if (responsables == null || responsables.isEmpty()) {
            log.debug("Aucun responsable à notifier pour la tâche {}", task.getId());
            return;
        }

        ModelNotifDTO modelNotif;
        try {
            modelNotif = configManageRestClient.getModelNotifByCode(MODEL_CODE);
        } catch (Exception e) {
            log.warn("ModelNotif '{}' introuvable, notification annulée : {}", MODEL_CODE, e.getMessage());
            return;
        }
        if (modelNotif == null) {
            log.warn("ModelNotif '{}' est null, notification annulée", MODEL_CODE);
            return;
        }

        for (ResponsableTacheDTO responsable : responsables) {
            try {
                String emailDestinataire = resolveEmail(responsable);
                String nomDestinataire = resolveName(responsable);

                if (emailDestinataire == null || emailDestinataire.isBlank()) {
                    log.debug("Pas d'email trouvé pour le responsable {}, notification ignorée", responsable);
                    continue;
                }

                Map<String, Object> ctx = buildContext(task, corresp, nomDestinataire, emailDestinataire);

                String renderedHtml = templateRenderService.render(modelNotif.getContent(), ctx);
                String renderedSubject = templateRenderService.render(
                    modelNotif.getSubjectFormula() != null ? modelNotif.getSubjectFormula() : "Nouvelle tâche assignée",
                    ctx
                );

                Message message = new Message();
                message.setSender(modelNotif.getSenderEmailFormule());
                message.setReceiver(List.of(Map.of("email", emailDestinataire)));
                message.setCorrespId(corresp != null ? corresp.getId() : null);
                message.setMessageType("EMAIL_NOTIF_TACHE");

                emailSendService.sendEmailDirectWithHtml(message, renderedHtml, renderedSubject);

                log.info("Notification tâche envoyée à {} pour tâche id={}", emailDestinataire, task.getId());

            } catch (Exception e) {
                log.error("Erreur notification tâche pour responsable {} : {}", responsable, e.getMessage(), e);
            }
        }
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

    private Map<String, Object> buildContext(Tasks task, Corresp corresp, String nomDestinataire, String emailDestinataire) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("typeTask",          task.getTaskType() != null ? task.getTaskType() : "");
        // objetTache = nom lisible du responsable + type de tâche (pas le code brut)
        String typeTask = task.getTaskType() != null ? task.getTaskType().toUpperCase() : "N/A";
        ctx.put("objetTache",        nomDestinataire + " effectuera la tâche de " + typeTask);
        ctx.put("nomDestinataire",   nomDestinataire);
        ctx.put("emailDestinataire", emailDestinataire);
        ctx.put("commentaire",       task.getCommentaire() != null ? task.getCommentaire() : "");
        ctx.put("dateLimite",        task.getDueDate() != null
            ? task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        ctx.put("dateAujourdhui",    ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (corresp != null) {
            ctx.put("referenceCorresp", corresp.getReference() != null ? corresp.getReference() : "");
            ctx.put("objetCorresp",     corresp.getSubject() != null ? corresp.getSubject() : "");
            // Lien direct vers la tâche dans le frontend
            ctx.put("lienTache", frontendUrl + "/corresp/" + corresp.getId() + "/view?taskId=" + task.getId());
        } else {
            ctx.put("referenceCorresp", "");
            ctx.put("objetCorresp",     "");
            ctx.put("lienTache", frontendUrl);
        }

        return ctx;
    }
}
