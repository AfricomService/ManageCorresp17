package com.africom.correspmanage.web.rest;

import com.africom.correspmanage.service.TaskEcheanceDigestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint appelé par n8n : retourne les mails de rappel d'échéance déjà rendus (FreeMarker).
 */
@RestController
@RequestMapping("/api/rappels")
public class TaskEcheanceDigestResource {

    private final Logger log = LoggerFactory.getLogger(TaskEcheanceDigestResource.class);

    private final TaskEcheanceDigestService digestService;

    public TaskEcheanceDigestResource(TaskEcheanceDigestService digestService) {
        this.digestService = digestService;
    }

    /**
     * GET /api/rappels/echeances?windowHours=24&overrideEmail=...
     * - windowHours   : fenêtre en heures (défaut 24) ; les tâches dépassées sont toujours incluses
     * - overrideEmail : (tests) envoie tous les mails à cette adresse au lieu des vrais responsables
     */
    @GetMapping("/echeances")
    public ResponseEntity<?> getEcheances(
        @RequestParam(name = "windowHours", defaultValue = "24") int windowHours,
        @RequestParam(name = "overrideEmail", required = false) String overrideEmail
    ) {
        if (windowHours < 1 || windowHours > 24 * 365) {
            return ResponseEntity.badRequest().body("windowHours doit être entre 1 et 8760");
        }
        try {
            return ResponseEntity.ok(digestService.buildDigestMails(windowHours, overrideEmail));
        } catch (IllegalStateException e) {
            log.error("Rappels d'échéance : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }
}
