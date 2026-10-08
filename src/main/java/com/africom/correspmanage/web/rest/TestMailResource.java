package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.client.ConfigManageRestClient;
import com.africom.correspmanage.client.ModelNotifDTO;
import com.africom.correspmanage.service.TemplateRenderService;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API TEMPORAIRE de test (n8n + FreeMarker). À supprimer après les tests.
 * Lit le ModelNotif NOTIF_TACHE depuis ConfigManage, le rend avec FreeMarker
 * et retourne l'email destinataire, le sujet et le HTML.
 */
@RestController
@RequestMapping("/api/test")
public class TestMailResource {

    private final Logger log = LoggerFactory.getLogger(TestMailResource.class);

    private static final String TEST_EMAIL = "mohamedamine.trabelsi@africomservices.com";
    private static final String MODEL_CODE = "TEST_N8N";

    private final TemplateRenderService templateRenderService;
    private final ConfigManageRestClient configManageRestClient;

    public TestMailResource(TemplateRenderService templateRenderService, ConfigManageRestClient configManageRestClient) {
        this.templateRenderService = templateRenderService;
        this.configManageRestClient = configManageRestClient;
    }

    /**
     * GET /api/test/mail : retourne { email, subject, html }.
     */
    @GetMapping("/mail")
    public ResponseEntity<Map<String, Object>> getTestMail() {
        log.debug("REST request to get test mail (n8n) from ModelNotif {}", MODEL_CODE);

        // 1) Récupérer le ModelNotif depuis ConfigManage (comme TaskNotifService)
        ModelNotifDTO modelNotif;
        try {
            modelNotif = configManageRestClient.getModelNotifByCode(MODEL_CODE);
        } catch (Exception e) {
            log.warn("ModelNotif '{}' introuvable : {}", MODEL_CODE, e.getMessage());
            return error(HttpStatus.BAD_GATEWAY, "ModelNotif '" + MODEL_CODE + "' introuvable : " + e.getMessage());
        }
        if (modelNotif == null || modelNotif.getContent() == null || modelNotif.getContent().isBlank()) {
            return error(HttpStatus.NOT_FOUND, "ModelNotif '" + MODEL_CODE + "' vide ou inexistant");
        }

        // 2) Variables du template (mêmes noms que TaskNotifService.buildContext)
        String nomDestinataire = "Mohamed Amine";
        String typeTask = "traitement";

        Map<String, Object> ctx = new HashMap<>();
        ctx.put("typeTask", typeTask);
        ctx.put("objetTache", nomDestinataire + " effectuera la tâche de " + typeTask.toUpperCase());
        ctx.put("nomDestinataire", nomDestinataire);
        ctx.put("emailDestinataire", TEST_EMAIL);
        ctx.put("commentaire", "Ceci est un commentaire de test");
        ctx.put("dateLimite", ZonedDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ctx.put("dateAujourdhui", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ctx.put("referenceCorresp", "AR-AF_26_00001");
        ctx.put("objetCorresp", "Facture fournisseur de test");
        ctx.put("lienTache", "http://localhost:9000");

        // 3) Rendu FreeMarker (sujet + corps)
        String subject;
        String html;
        try {
            subject = templateRenderService.render(
                modelNotif.getSubjectFormula() != null ? modelNotif.getSubjectFormula() : "Nouvelle tâche assignée",
                ctx
            );
            html = templateRenderService.render(modelNotif.getContent(), ctx);
        } catch (Exception e) {
            log.error("Erreur rendu FreeMarker : {}", e.getMessage(), e);
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur rendu FreeMarker : " + e.getMessage());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("email", TEST_EMAIL);
        result.put("subject", subject);
        result.put("html", html);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
