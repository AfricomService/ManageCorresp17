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
}
