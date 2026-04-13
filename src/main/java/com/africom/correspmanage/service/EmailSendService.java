package com.africom.correspmanage.service;

import com.africom.correspmanage.config.ApplicationProperties;
import com.africom.correspmanage.domain.Corresp;
import com.africom.correspmanage.domain.Message;
import com.africom.correspmanage.domain.Pj;
import com.africom.correspmanage.domain.SmtpConfig;
import com.africom.correspmanage.repository.CorrespRepository;
import com.africom.correspmanage.repository.MessageRepository;
import com.africom.correspmanage.repository.SmtpConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class EmailSendService {

    private final Logger log = LoggerFactory.getLogger(EmailSendService.class);

    private final MessageRepository     messageRepository;
    private final SmtpConfigRepository smtpConfigRepository;
    private final CorrespRepository     correspRepository;

    public EmailSendService(
        MessageRepository     messageRepository,
        CorrespRepository     correspRepository,
        SmtpConfigRepository  smtpConfigRepository
    ) {
        this.messageRepository    = messageRepository;
        this.correspRepository    = correspRepository;
        this.smtpConfigRepository = smtpConfigRepository;
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

// ── 5) Construire et envoyer via JavaMail ─────────────────────────
        try {
            SmtpConfig smtpConfig = resolveSmtpConfig(null); // prend la 1ère active
            Session session = buildMailSession(smtpConfig);

            MimeMessage mimeMessage = new MimeMessage(session);

            // From — dynamique depuis message.getSender()
            String fromName = smtpConfig.getFromName() != null
                ? smtpConfig.getFromName() : "CorrespCare";
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
// Corps HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

            MimeMultipart multipart = new MimeMultipart("mixed");
            multipart.addBodyPart(htmlPart);

// ── PJs de la correspondance ──────────────────────────────────────
// On récupère les pièces jointes via le correspId du Message
            attachPjsFromCorresp(message.getCorrespId(), multipart);

            mimeMessage.setContent(multipart);
            mimeMessage.saveChanges();

// Envoi via la config SMTP en base
            Transport transport = session.getTransport("smtp");
            transport.connect(
                smtpConfig.getSmtpHost(),
                smtpConfig.getSmtpPort(),
                Boolean.TRUE.equals(smtpConfig.getUseAuth()) ? smtpConfig.getUsername() : null,
                Boolean.TRUE.equals(smtpConfig.getUseAuth()) ? smtpConfig.getPassword() : null
            );
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();

            // Récupérer le Message-ID généré par JavaMail
            String smtpMessageId = mimeMessage.getMessageID(); // MimeMessage.getMessageID()

// Persister le msgID dans la table message
            message.setMsgID(smtpMessageId);
            messageRepository.save(message);

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

        // Récupérer la config SMTP depuis la base (première active)
        SmtpConfig smtpConfig = resolveSmtpConfig(null);

        log.info("=== TEST SMTP | host={} | port={} | to={} ===",
            smtpConfig.getSmtpHost(), smtpConfig.getSmtpPort(), to);

        try {
            Session session = buildMailSession(smtpConfig);

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(
                smtpConfig.getFromAddress() != null
                    ? smtpConfig.getFromAddress() : "noreply@africomservices.com",
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
                    + smtpConfig.getSmtpHost() + ":" + smtpConfig.getSmtpPort()
                    + "</p>",
                "text/html; charset=UTF-8"
            );
            mimeMessage.saveChanges();

            Transport transport = session.getTransport("smtp");
            transport.connect(
                smtpConfig.getSmtpHost(),
                smtpConfig.getSmtpPort(),
                Boolean.TRUE.equals(smtpConfig.getUseAuth()) ? smtpConfig.getUsername() : null,
                Boolean.TRUE.equals(smtpConfig.getUseAuth()) ? smtpConfig.getPassword() : null
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
    private Session buildMailSession(SmtpConfig conf) {
        Properties props = new Properties();
        props.put("mail.smtp.host",              conf.getSmtpHost());
        props.put("mail.smtp.port",              String.valueOf(conf.getSmtpPort()));
        props.put("mail.smtp.auth",              String.valueOf(conf.getUseAuth()));
        props.put("mail.smtp.starttls.enable",   String.valueOf(conf.getUseTls()));
        props.put("mail.smtp.ssl.enable",        String.valueOf(conf.getUseSsl()));
        props.put("mail.smtp.connectiontimeout", String.valueOf(conf.getConnectionTimeout()));
        props.put("mail.smtp.timeout",           String.valueOf(conf.getTimeout()));
        props.put("mail.smtp.writetimeout",      String.valueOf(conf.getTimeout()));
        props.put("mail.debug",                  "true");

        if (conf.getLacalhostName() != null && !conf.getLacalhostName().isBlank()) {
            props.put("mail.smtp.localhost", conf.getLacalhostName());
        }

        if (Boolean.TRUE.equals(conf.getUseAuth())) {
            // Avec authentification
            return Session.getInstance(props, new javax.mail.Authenticator() {
                @Override
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(
                        conf.getUsername(), conf.getPassword()
                    );
                }
            });
        } else {
            // Sans authentification (cas topnet)
            return Session.getInstance(props);
        }
    }

    // ── Récupérer la config SMTP (par nom ou la première active) ──────────
    private SmtpConfig resolveSmtpConfig(String configName) {
        if (configName != null && !configName.isBlank()) {
            return smtpConfigRepository.findByNameAndActiveTrue(configName)
                .orElseThrow(() -> new RuntimeException(
                    "Config SMTP introuvable : " + configName));
        }
        return smtpConfigRepository.findFirstByActiveTrue()
            .orElseThrow(() -> new RuntimeException(
                "Aucune configuration SMTP active en base"));
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

    /**
     * Récupère les PJs de la correspondance et les attache au MimeMultipart.
     * Si une PJ n'existe pas sur le disque, elle est ignorée avec un warning.
     *
     * @param correspId  id de la correspondance (depuis message.getCorrespId())
     * @param multipart  le multipart MIME auquel on va ajouter les attachments
     */
    private void attachPjsFromCorresp(Long correspId, MimeMultipart multipart)
        throws Exception {

        if (correspId == null) {
            log.warn("correspId null — aucune PJ attachée");
            return;
        }

        // Récupérer la correspondance avec ses PJs (eager load via JPA)
        Corresp corresp = correspRepository.findOneWithEagerRelationshipss(correspId)
            .orElse(null);

        if (corresp == null) {
            log.warn("Corresp introuvable pour correspId={} — aucune PJ attachée", correspId);
            return;
        }

        Set<Pj> pjs = corresp.getAttachments();

        if (pjs == null || pjs.isEmpty()) {
            log.info("Aucune PJ pour correspId={}", correspId);
            return;
        }

        log.info("{} PJ(s) trouvée(s) pour correspId={}", pjs.size(), correspId);

        for (Pj pj : pjs) {
            String filePath = pj.getPath();

            if (filePath == null || filePath.isBlank()) {
                log.warn("PJ id={} — path null ou vide, ignorée", pj.getId());
                continue;
            }

            File file = new File(filePath);

            if (!file.exists() || !file.isFile()) {
                log.warn("PJ id={} — fichier introuvable : {}", pj.getId(), filePath);
                continue;
            }

            // Construire le nom d'affichage de la pièce jointe
            String attachmentName = buildAttachmentName(pj);

            try {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setDataHandler(
                    new DataHandler(new FileDataSource(file))
                );
                // Nom du fichier visible dans le client email (encodé UTF-8)
                attachmentPart.setFileName(
                    javax.mail.internet.MimeUtility.encodeText(attachmentName, "UTF-8", "B")
                );
                multipart.addBodyPart(attachmentPart);
                log.info("✅ PJ attachée : {} ({})", attachmentName, filePath);
            } catch (Exception e) {
                log.error("❌ Impossible d'attacher la PJ id={} : {}", pj.getId(), e.getMessage());
                // On continue les autres PJs même si l'une échoue
            }
        }
    }

    /**
     * Construit le nom complet du fichier attachment à partir de l'entité Pj.
     * Exemple : "MonDocument.pdf"
     */
    private String buildAttachmentName(Pj pj) {
        String name = pj.getName() != null ? pj.getName().trim() : "document";
        String ext  = pj.getFileType() != null ? pj.getFileType().trim() : "";

        if (ext.isEmpty()) {
            return name;
        }
        // Eviter double extension si name contient déjà l'extension
        if (name.toLowerCase().endsWith("." + ext.toLowerCase())) {
            return name;
        }
        return name + "." + ext;
    }
}
