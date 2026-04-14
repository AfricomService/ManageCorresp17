package com.africom.correspmanage.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.SmtpConfig} entity.
 */
public class SmtpConfigDTO implements Serializable {

    private Long id;

    private String name;

    private String smtpHost;

    private Integer smtpPort;

    private String username;

    private String password;

    private Boolean useAuth;

    private Boolean useTls;

    private Boolean useSsl;

    private String fromAddress;

    private String fromName;

    private String lacalhostName;

    private Integer connectionTimeout;

    private Integer timeout;

    private Boolean active;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUseAuth() {
        return useAuth;
    }

    public void setUseAuth(Boolean useAuth) {
        this.useAuth = useAuth;
    }

    public Boolean getUseTls() {
        return useTls;
    }

    public void setUseTls(Boolean useTls) {
        this.useTls = useTls;
    }

    public Boolean getUseSsl() {
        return useSsl;
    }

    public void setUseSsl(Boolean useSsl) {
        this.useSsl = useSsl;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getLacalhostName() {
        return lacalhostName;
    }

    public void setLacalhostName(String lacalhostName) {
        this.lacalhostName = lacalhostName;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmtpConfigDTO)) {
            return false;
        }

        SmtpConfigDTO smtpConfigDTO = (SmtpConfigDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, smtpConfigDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SmtpConfigDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", smtpHost='" + getSmtpHost() + "'" +
            ", smtpPort=" + getSmtpPort() +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", useAuth='" + getUseAuth() + "'" +
            ", useTls='" + getUseTls() + "'" +
            ", useSsl='" + getUseSsl() + "'" +
            ", fromAddress='" + getFromAddress() + "'" +
            ", fromName='" + getFromName() + "'" +
            ", lacalhostName='" + getLacalhostName() + "'" +
            ", connectionTimeout=" + getConnectionTimeout() +
            ", timeout=" + getTimeout() +
            ", active='" + getActive() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
