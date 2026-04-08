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

    private final Mail mail = new Mail();

    public Mail getMail() { return mail; }

    public static class Mail {
        private String fromName = "CorrespCare";
        public String getFromName() { return fromName; }
        public void setFromName(String fromName) { this.fromName = fromName; }
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
}
