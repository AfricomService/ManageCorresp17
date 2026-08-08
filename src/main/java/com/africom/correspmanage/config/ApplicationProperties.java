package com.africom.correspmanage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Corresp Manage.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String slash;
    private String dir_pj;
    private String dir_body;

//    private String frontendUrl = "https://172.30.1.111/cc";
    private String frontendUrl = "http://localhost:9000";       //valeur par défaut pour le dev local

    private final Mail mail = new Mail();

    public Mail getMail() { return mail; }

    public static class Mail {

        private String fromName = "CorrespCare";

        // SMTP à utiliser pour l'envoi (topnet dans votre cas)
        private String smtpHost = "smtp.topnet.tn";
        private int    smtpPort = 25;

        // Compte SMTP d'authentification (celui qui a accès au relay topnet)
        private String smtpUsername = "";
        private String smtpPassword = "";

        // Activer STARTTLS ou pas (topnet:25 → false)
        private boolean starttls = false;

        public String getFromName()               { return fromName; }
        public void   setFromName(String v)       { this.fromName = v; }

        public String getSmtpHost()               { return smtpHost; }
        public void   setSmtpHost(String v)       { this.smtpHost = v; }

        public int    getSmtpPort()               { return smtpPort; }
        public void   setSmtpPort(int v)          { this.smtpPort = v; }

        public String getSmtpUsername()           { return smtpUsername; }
        public void   setSmtpUsername(String v)   { this.smtpUsername = v; }

        public String getSmtpPassword()           { return smtpPassword; }
        public void   setSmtpPassword(String v)   { this.smtpPassword = v; }

        public boolean isStarttls()               { return starttls; }
        public void    setStarttls(boolean v)     { this.starttls = v; }
    }

    public String getSlash() {
        return slash;
    }

    public void setSlash(String slash) {
        this.slash = slash;
    }

    public String getDir_pj() {
        return dir_pj;
    }

    public void setDir_pj(String dir_pj) {
        this.dir_pj = dir_pj;
    }

    public String getDir_body() {
        return dir_body;
    }

    public void setDir_body(String dir_body) {
        this.dir_body = dir_body;
    }

    public String getFrontendUrl() { return frontendUrl; }
    public void setFrontendUrl(String frontendUrl) { this.frontendUrl = frontendUrl; }
}
