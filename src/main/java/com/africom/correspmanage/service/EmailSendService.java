package com.africom.correspmanage.service;

import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Transactional
public class EmailSendService {

    private final Logger log = LoggerFactory.getLogger(EmailSendService.class);

    // ── Injecter l'INTERFACE, pas la classe concrète ──────────────────────
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
     * Méthode de test — tout hardcodé, utilise javax.mail directement
     * pour contourner les problèmes d'authentification de Spring Mail.
     */
    public String sendTestEmailWithStaticConfig() {

        final String SMTP_HOST = "smtp.topnet.tn";
        final int    SMTP_PORT = 25;
        final String FROM      = "boc@africomservices.com";
        final String TO        = "trabelsimedamine999@gmail.com";
        final String SUBJECT   = "Test SMTP depuis Spring Boot";
        final String BODY      = "Hello world – envoyé depuis Spring Boot JHipster !";

        log.info("Envoi en cours — test SANS authentification...");

        Properties props = new Properties();
        props.put("mail.smtp.host",              SMTP_HOST);
        props.put("mail.smtp.port",              String.valueOf(SMTP_PORT));
        props.put("mail.smtp.auth",              "false");  // ← SANS AUTH
        props.put("mail.smtp.starttls.enable",   "false");
        props.put("mail.smtp.ssl.enable",        "false");
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.smtp.timeout",           "30000");
        props.put("mail.smtp.writetimeout",      "30000");
        props.put("mail.smtp.localhost",         "africomservices.com");
        props.put("mail.debug",                  "true");

        // ── Session SANS Authenticator ────────────────────────────────────────
        Session session = Session.getInstance(props);
        session.setDebug(true);

        Transport transport = null;
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
            message.setSubject(SUBJECT, "UTF-8");
            message.setText(BODY, "UTF-8");
            message.saveChanges();

            // Connexion SANS credentials
            transport = session.getTransport("smtp");
            transport.connect(SMTP_HOST, SMTP_PORT, null, null);
            transport.sendMessage(message, message.getAllRecipients());

            String success = "✅ Email envoyé avec succès à " + TO;
            log.info(success);
            return success;

        } catch (MessagingException e) {
            String error = "❌ Échec de l'envoi : " + e.getMessage();
            log.error(error, e);
            return error;
        } finally {
            if (transport != null) {
                try { transport.close(); } catch (MessagingException ignored) {}
            }
        }
    }
}
