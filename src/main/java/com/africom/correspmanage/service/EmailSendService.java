package com.africom.correspmanage.service;

import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.repository.MessageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EmailSendService {

    private final Logger log = LoggerFactory.getLogger(EmailSendService.class);

    private final JavaMailSender        mailSender;
    private final MessageRepository     messageRepository;
    private final ObjectMapper          objectMapper;
    private final ApplicationProperties applicationProperties;

    public EmailSendService(
        JavaMailSender        mailSender,
        MessageRepository     messageRepository,
        ObjectMapper          objectMapper,
        ApplicationProperties applicationProperties
    ) {
        this.mailSender            = mailSender;
        this.messageRepository     = messageRepository;
        this.objectMapper          = objectMapper;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Envoie l'email SMTP depuis le Message persisté (messageId).
     *
     * ── Sender dynamique ──
     * L'adresse expéditeur est lue directement depuis message.getSender().
     * Aucune adresse fixe dans application.yml n'est requise.
     *
     * @param messageId  id du Message en base
     * @param subject    objet de l'email (rendu FreeMarker)
     * @return           le Message mis à jour (etat = SENT ou FAILED)
     */
    public Message sendEmailFromMessage(Long messageId, String subject) {

        // ── 1) Récupérer le Message depuis la base ───────────────────────
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Message introuvable avec id: " + messageId));

        // ── 2) Lire le HTML depuis le fichier persisté ───────────────────
        String htmlContent;
        try {
            htmlContent = Files.readString(Paths.get(message.getPath()));
        } catch (Exception e) {
            log.error("Impossible de lire le fichier HTML du message {}: {}",
                messageId, message.getPath(), e);
            updateMessageEtat(message, "FAILED");
            throw new RuntimeException(
                "Fichier HTML introuvable : " + message.getPath());
        }

        // ── 3) Récupérer l'email expéditeur depuis le Message ────────────
        //       On utilise message.getSender() — pas de valeur fixe en config.
        //       Si le sender est null ou vide, on lève une exception métier.
        String senderEmail = message.getSender();
        if (senderEmail == null || senderEmail.isBlank()) {
            log.error("Sender vide pour messageId={}", messageId);
            updateMessageEtat(message, "FAILED");
            throw new RuntimeException(
                "Expéditeur (sender) non renseigné dans le message " + messageId);
        }
        senderEmail = senderEmail.trim().toLowerCase();

        // Nom affiché de l'expéditeur (depuis ApplicationProperties)
        String fromName = applicationProperties.getMail().getFromName();

        // ── 4) Parser les destinataires (TO et CC) ───────────────────────
        List<String> toEmails = parseEmailListFromJson(message.getReceiver());
        List<String> ccEmails = parseEmailListFromJson(message.getCopyTo());

        if (toEmails.isEmpty()) {
            log.warn("Aucun destinataire valide pour messageId={}", messageId);
            updateMessageEtat(message, "FAILED");
            throw new RuntimeException(
                "Aucun destinataire (receiver) valide pour le message " + messageId);
        }

        log.debug("Envoi email messageId={} | from={} | to={} | cc={} | subject={}",
            messageId, senderEmail, toEmails, ccEmails, subject);

        // ── 5) Construire et envoyer le MimeMessage SMTP ─────────────────
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // From — dynamique depuis message.getSender()
            helper.setFrom(new InternetAddress(senderEmail, fromName, "UTF-8"));

            // Subject
            helper.setSubject(subject != null ? subject : "Correspondance");

            // To
            helper.setTo(toEmails.toArray(new String[0]));

            // CC (optionnel)
            if (!ccEmails.isEmpty()) {
                helper.setCc(ccEmails.toArray(new String[0]));
            }

            // Corps HTML
            helper.setText(htmlContent, true /* isHtml */);

            // Envoi
            mailSender.send(mimeMessage);

            log.info("✅ Email envoyé pour messageId={} | from={} | to={}",
                messageId, senderEmail, toEmails);

            // ── 6) Mettre à jour le statut en SENT ────────────────────────
            return updateMessageEtat(message, "SENT");

        } catch (Exception e) {
            log.error("❌ Échec envoi email pour messageId={} | from={}",
                messageId, senderEmail, e);
            updateMessageEtat(message, "FAILED");
            throw new RuntimeException(
                "Échec de l'envoi SMTP : " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privés
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Met à jour le champ etat du Message et le sauvegarde.
     */
    private Message updateMessageEtat(Message message, String etat) {
        message.setEtat(etat);
        message.setUpdatedDate(ZonedDateTime.now());
        return messageRepository.save(message);
    }

    /**
     * Parse le champ receiver / copyTo stocké sous forme de
     * List<Map<String,String>> en JSON : [{"email":"a@b.com"}, ...]
     *
     * Accepte aussi une String simple séparée par virgule : "a@b.com,c@d.com"
     */
    private List<String> parseEmailListFromJson(
        List<Map<String, String>> jsonList
    ) {
        List<String> emails = new ArrayList<>();
        if (jsonList == null || jsonList.isEmpty()) return emails;

        for (Map<String, String> entry : jsonList) {
            String email = entry.get("email");
            if (email != null && !email.isBlank()) {
                emails.add(email.trim().toLowerCase());
            }
        }
        return emails;
    }
}
