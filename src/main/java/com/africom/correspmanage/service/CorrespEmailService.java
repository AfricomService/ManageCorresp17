package com.africom.correspmanage.service;

import com.africom.correspmanage.client.ConfigManageRestClient;
import com.africom.correspmanage.client.ModelNotifDTO;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.MessageRepository;
import com.africom.correspmanage.security.SecurityUtils;
import com.africom.correspmanage.service.dto.SendCorrespEmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class CorrespEmailService {

    private final Logger log = LoggerFactory.getLogger(CorrespEmailService.class);

    private final CorrespRepository correspRepository;
    private final MessageRepository messageRepository;
    private final ConfigManageRestClient configManageRestClient;
    private final TemplateRenderService templateRenderService;
    private final CorrespService correspService;

    public CorrespEmailService(
        CorrespRepository correspRepository,
        MessageRepository messageRepository,
        ConfigManageRestClient configManageRestClient,
        TemplateRenderService templateRenderService,
        CorrespService correspService
    ) {
        this.correspRepository = correspRepository;
        this.messageRepository = messageRepository;
        this.configManageRestClient = configManageRestClient;
        this.templateRenderService = templateRenderService;
        this.correspService = correspService;
    }

    /**
     * Prépare et persiste le message email d'une correspondance sortante
     * en utilisant FreeMarker pour rendre le template du ModelNotif.
     */
    public Message prepareAndPersistEmail(SendCorrespEmailRequestDTO request) {

        // 1) Récupérer la correspondance
        Corresp corresp = correspRepository.findById(request.getCorrespId())
            .orElseThrow(() -> new EntityNotFoundException(
                "Corresp not found with id: " + request.getCorrespId()));

        // 2) Récupérer le ModelNotif depuis configmanage via Feign
        ModelNotifDTO modelNotif = configManageRestClient.getModelNotifById(request.getModelNotifId());
        if (modelNotif == null) {
            throw new EntityNotFoundException(
                "ModelNotif not found with id: " + request.getModelNotifId());
        }

        // 3) Construire les variables du contexte FreeMarker
        Map<String, Object> ctx = buildTemplateContext(request, corresp);

        // 4) Rendre le template (content du ModelNotif)
        String renderedHtml = templateRenderService.render(modelNotif.getContent(), ctx);

        // 5) Rendre la formule d'objet
        String renderedSubject = templateRenderService.render(
            modelNotif.getSubjectFormula() != null ? modelNotif.getSubjectFormula() : corresp.getSubject(),
            ctx
        );

        // 6) Persister dans la table message
        //    On cherche s'il existe déjà un message pour cette corresp
        Message message = messageRepository.findByCorrespId(corresp.getId())
            .orElse(new Message());

        message.setSender(
            request.getSenderEmail() != null
                ? request.getSenderEmail()
                : modelNotif.getSenderEmailFormule()
        );
        message.setCorrespId(corresp.getId());
        message.setType(modelNotif.getName());
        message.setEtat("PENDING");
        message.setCreatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));

        // receiver (conversion String → List<Map>)
        message.setReceiver(parseEmailString(request.getReceiver()));

        // copyTo (conversion String → List<Map>)
        message.setCopyTo(parseEmailString(request.getCopyTo()));

        // Sauvegarder le HTML rendu dans un fichier (réutilise la méthode existante)
        String filePath = correspService.saveBodyToFile(renderedHtml, corresp.getId());
        message.setPath(filePath);
        message.setUniqueName(corresp.getId() + "_email.html");

        if (message.getCreationDate() == null) {
            message.setCreationDate(ZonedDateTime.now());
        }
        message.setUpdatedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
        message.setUpdatedDate(ZonedDateTime.now());

        Message saved = messageRepository.save(message);
        log.info("Message email persisté pour correspId={}, messageId={}", corresp.getId(), saved.getId());

        return saved;
    }

    /**
     * Construit le Map des variables disponibles dans le template FreeMarker.
     * TOUTES les variables documentées dans le modèle doivent être ajoutées ici.
     */
    private Map<String, Object> buildTemplateContext(SendCorrespEmailRequestDTO req, Corresp corresp) {
        Map<String, Object> ctx = new HashMap<>();

        // ── Correspondance
        ctx.put("reference",    corresp.getReference() != null ? corresp.getReference() : "");
        ctx.put("objet",        corresp.getSubject() != null ? corresp.getSubject() : "");
        ctx.put("date",         corresp.getCourrierDate() != null
            ? corresp.getCourrierDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            : "");
        ctx.put("destinataire", corresp.getLabelDestinataire() != null ? corresp.getLabelDestinataire() : "");

        // ── Corps de la correspondance (saisi dans l'éditeur HTML)
        ctx.put("corps",        req.getCorrespBody() != null ? req.getCorrespBody() : "");

        // ── Expéditeur / Signature
        ctx.put("expediteur",   corresp.getSender() != null ? corresp.getSender() : "");
        ctx.put("nomComplet",   req.getSenderFullName() != null ? req.getSenderFullName() : "");
        ctx.put("departement",  req.getSenderDepartement() != null ? req.getSenderDepartement() : "");
        ctx.put("emailEmetteur",req.getSenderEmail() != null ? req.getSenderEmail() : "");

        // ── Destinataires email
        ctx.put("emailDestinataire", req.getReceiver() != null ? req.getReceiver() : "");
        ctx.put("emailCopie",        req.getCopyTo() != null ? req.getCopyTo() : "");

        // ── Date courante
        ctx.put("dateAujourdhui",
            ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return ctx;
    }

    /**
     * Convertit une chaîne d'emails séparés par virgule
     * en List<Map<String,String>> compatible avec Message.receiver / copyTo.
     */
    private List<Map<String, String>> parseEmailString(String emailStr) {
        List<Map<String, String>> result = new ArrayList<>();
        if (emailStr == null || emailStr.isBlank()) return result;

        String[] parts = emailStr.split("[,;]+");
        for (String part : parts) {
            String email = part.trim().toLowerCase();
            if (!email.isEmpty()) {
                Map<String, String> entry = new HashMap<>();
                entry.put("email", email);
                result.add(entry);
            }
        }
        return result;
    }
}
