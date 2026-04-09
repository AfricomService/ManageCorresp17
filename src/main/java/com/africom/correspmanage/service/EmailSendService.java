package com.africom.correspmanage.service;

import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
@Transactional
public class EmailSendService {

    private final Logger log = LoggerFactory.getLogger(EmailSendService.class);

    private final MessageRepository     messageRepository;
    private final ApplicationProperties applicationProperties;

    // Plus de JavaMailSender Spring Boot — on gère notre propre session JavaMail
    public EmailSendService(
        MessageRepository     messageRepository,
        ApplicationProperties applicationProperties
    ) {
        this.messageRepository     = messageRepository;
        this.applicationProperties = applicationProperties;
    }

    // =========================================================================
    // MÉTHODE PRINCIPALE — envoi depuis un Message persisté
    // =========================================================================

    /**
     * Envoie l'email SMTP à partir du Message persisté en base.
     *
     * Sender dynamique : message.getSender() → From de l'email
     * SMTP             : smtp.topnet.tn:25 (depuis ApplicationProperties)
     *
     * @param messageId  id du Message en base
     * @param subject    objet de l'email
     * @return           le Message mis à jour (etat = SENT ou FAILED)
     */
    public Message sendEmailFromMessage(Long messageId, String subject) {

        // ── 1) Récupérer le Message depuis la base ────────────────────────
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Message introuvable : id=" + messageId));

        // ── 2) Lire le HTML depuis le fichier persisté ────────────────────
        String htmlContent;
        try {
            htmlContent = Files.readString(Paths.get(message.getPath()));
        } catch (Exception e) {
            log.error("Impossible de lire le fichier HTML: {}", message.getPath(), e);
            return updateMessageEtat(message, "FAILED");
        }

        // ── 3) Récupérer l'email expéditeur depuis message.getSender() ────
        //       C'est ici le cœur du "sender dynamique" :
        //       chaque correspondance a son propre expéditeur.
        String senderEmail = message.getSender();
        if (senderEmail == null || senderEmail.isBlank()) {
            log.error("Sender vide pour messageId={}", messageId);
            return updateMessageEtat(message, "FAILED");
        }
        senderEmail = senderEmail.trim();

        // ── 4) Parser les destinataires ───────────────────────────────────
        List<String> toEmails = parseEmailList(message.getReceiver());
        List<String> ccEmails = parseEmailList(message.getCopyTo());

        if (toEmails.isEmpty()) {
            log.error("Aucun destinataire pour messageId={}", messageId);
            return updateMessageEtat(message, "FAILED");
        }

        log.info(">>> Envoi email | messageId={} | from={} | to={} | cc={}",
            messageId, senderEmail, toEmails, ccEmails);

        // ── 5) Construire et envoyer via JavaMail (smtp.topnet.tn:25) ─────
        try {
            Session session = buildMailSession();

            MimeMessage mimeMessage = new MimeMessage(session);

            // From — dynamique depuis message.getSender()
            String fromName = applicationProperties.getMail().getFromName();
            mimeMessage.setFrom(
                new InternetAddress(senderEmail, fromName, "UTF-8")
            );

            // Subject
            mimeMessage.setSubject(
                subject != null ? subject : "Correspondance", "UTF-8"
            );

            // To
            Address[] toAddresses = toInternetAddresses(toEmails);
            mimeMessage.setRecipients(
                javax.mail.Message.RecipientType.TO, toAddresses
            );

            // CC (optionnel)
            if (!ccEmails.isEmpty()) {
                Address[] ccAddresses = toInternetAddresses(ccEmails);
                mimeMessage.setRecipients(
                    javax.mail.Message.RecipientType.CC, ccAddresses
                );
            }

            // Corps HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

            MimeMultipart multipart = new MimeMultipart("mixed");
            multipart.addBodyPart(htmlPart);
            mimeMessage.setContent(multipart);

            mimeMessage.saveChanges();

            // Envoi via SMTP topnet
            ApplicationProperties.Mail mailConf =
                applicationProperties.getMail();

// APRÈS — connexion SANS credentials, comme le test hardcodé
            Transport transport = session.getTransport("smtp");
            transport.connect(
                mailConf.getSmtpHost(),
                mailConf.getSmtpPort(),
                null,    // ← null au lieu de username
                null     // ← null au lieu de password
            );
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();

            log.info("✅ Email envoyé | messageId={} | from={} | to={}",
                messageId, senderEmail, toEmails);

            return updateMessageEtat(message, "SENT");

        } catch (Exception e) {
            log.error("❌ Échec envoi SMTP | messageId={} | erreur={}",
                messageId, e.getMessage(), e);
            return updateMessageEtat(message, "FAILED");
        }
    }

    // =========================================================================
    // MÉTHODE DE TEST — vérifie que smtp.topnet.tn fonctionne
    // =========================================================================

    /**
     * Test rapide : envoie un email simple pour vérifier la config SMTP.
     * Utilise les paramètres de ApplicationProperties (application-dev.yml).
     *
     * @param to  adresse de destination du test
     */
    public void sendTestEmail(String to) {
        ApplicationProperties.Mail mailConf = applicationProperties.getMail();

        log.info("=== TEST SMTP | host={} | port={} | user={} | to={} ===",
            mailConf.getSmtpHost(),
            mailConf.getSmtpPort(),
            mailConf.getSmtpUsername(),
            to);

        try {
            Session session = buildMailSession();

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(
                mailConf.getSmtpUsername(),
                "CorrespCare Test",
                "UTF-8"
            ));
            mimeMessage.setRecipients(
                javax.mail.Message.RecipientType.TO,
                InternetAddress.parse(to)
            );
            mimeMessage.setSubject("✅ Test CorrespCare — " + ZonedDateTime.now(), "UTF-8");
            mimeMessage.setContent(
                "<h2>Test OK</h2><p>SMTP: "
                    + mailConf.getSmtpHost() + ":" + mailConf.getSmtpPort()
                    + "</p>",
                "text/html; charset=UTF-8"
            );
            mimeMessage.saveChanges();

            Transport transport = session.getTransport("smtp");
            transport.connect(
                mailConf.getSmtpHost(),
                mailConf.getSmtpPort(),
                null,
                null
            );
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();

            log.info("✅ Test email envoyé à {}", to);

        } catch (Exception e) {
            log.error("❌ Échec test SMTP : {}", e.getMessage(), e);
            throw new RuntimeException("Échec test SMTP : " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // HELPERS PRIVÉS
    // =========================================================================

    /**
     * Construit la session JavaMail à partir des propriétés ApplicationProperties.
     * Correspond exactement à ce qui fonctionnait dans sendTestEmailHardcoded().
     */
    private Session buildMailSession() {
        ApplicationProperties.Mail conf = applicationProperties.getMail();

        Properties props = new Properties();
        props.put("mail.smtp.host",              conf.getSmtpHost());
        props.put("mail.smtp.port",              String.valueOf(conf.getSmtpPort()));
        props.put("mail.smtp.auth",              "false");          // ← SANS AUTH comme dans le test
        props.put("mail.smtp.starttls.enable",   "false");
        props.put("mail.smtp.ssl.enable",        "false");
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.smtp.timeout",           "30000");
        props.put("mail.smtp.writetimeout",      "30000");
        props.put("mail.smtp.localhost",         "africomservices.com");
        props.put("mail.debug",                  "true");

        // Session SANS Authenticator — exactement comme sendTestEmailWithStaticConfig()
        Session session = Session.getInstance(props);
        session.setDebug(true);
        return session;
    }

    /**
     * Met à jour le champ etat du Message et le sauvegarde.
     */
    private Message updateMessageEtat(Message message, String etat) {
        message.setEtat(etat);
        message.setUpdatedDate(ZonedDateTime.now());
        return messageRepository.save(message);
    }

    /**
     * Parse la liste de destinataires (format JSON en base : [{email: "..."}, ...])
     */
    private List<String> parseEmailList(List<Map<String, String>> jsonList) {
        List<String> emails = new ArrayList<>();
        if (jsonList == null) return emails;
        for (Map<String, String> entry : jsonList) {
            String email = entry.get("email");
            if (email != null && !email.isBlank()) {
                emails.add(email.trim());
            }
        }
        return emails;
    }

    /**
     * Convertit une liste de strings email en tableau d'Address JavaMail.
     */
    private Address[] toInternetAddresses(List<String> emails)
        throws Exception {
        Address[] addresses = new Address[emails.size()];
        for (int i = 0; i < emails.size(); i++) {
            addresses[i] = new InternetAddress(emails.get(i));
        }
        return addresses;
    }
}
