package com.africom.correspmanage.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A SmtpConfig.
 */
@Entity
@Table(name = "smtp_config")
public class SmtpConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "smtp_host")
    private String smtpHost;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "use_auth")
    private Boolean useAuth;

    @Column(name = "use_tls")
    private Boolean useTls;

    @Column(name = "use_ssl")
    private Boolean useSsl;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "from_name")
    private String fromName;

    @Column(name = "lacalhost_name")
    private String lacalhostName;

    @Column(name = "connection_timeout")
    private Integer connectionTimeout;

    @Column(name = "timeout")
    private Integer timeout;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SmtpConfig id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SmtpConfig name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmtpHost() {
        return this.smtpHost;
    }

    public SmtpConfig smtpHost(String smtpHost) {
        this.setSmtpHost(smtpHost);
        return this;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {
        return this.smtpPort;
    }

    public SmtpConfig smtpPort(Integer smtpPort) {
        this.setSmtpPort(smtpPort);
        return this;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getUsername() {
        return this.username;
    }

    public SmtpConfig username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public SmtpConfig password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getUseAuth() {
        return this.useAuth;
    }

    public SmtpConfig useAuth(Boolean useAuth) {
        this.setUseAuth(useAuth);
        return this;
    }

    public void setUseAuth(Boolean useAuth) {
        this.useAuth = useAuth;
    }

    public Boolean getUseTls() {
        return this.useTls;
    }

    public SmtpConfig useTls(Boolean useTls) {
        this.setUseTls(useTls);
        return this;
    }

    public void setUseTls(Boolean useTls) {
        this.useTls = useTls;
    }

    public Boolean getUseSsl() {
        return this.useSsl;
    }

    public SmtpConfig useSsl(Boolean useSsl) {
        this.setUseSsl(useSsl);
        return this;
    }

    public void setUseSsl(Boolean useSsl) {
        this.useSsl = useSsl;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public SmtpConfig fromAddress(String fromAddress) {
        this.setFromAddress(fromAddress);
        return this;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return this.fromName;
    }

    public SmtpConfig fromName(String fromName) {
        this.setFromName(fromName);
        return this;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getLacalhostName() {
        return this.lacalhostName;
    }

    public SmtpConfig lacalhostName(String lacalhostName) {
        this.setLacalhostName(lacalhostName);
        return this;
    }

    public void setLacalhostName(String lacalhostName) {
        this.lacalhostName = lacalhostName;
    }

    public Integer getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public SmtpConfig connectionTimeout(Integer connectionTimeout) {
        this.setConnectionTimeout(connectionTimeout);
        return this;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public SmtpConfig timeout(Integer timeout) {
        this.setTimeout(timeout);
        return this;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean getActive() {
        return this.active;
    }

    public SmtpConfig active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return this.description;
    }

    public SmtpConfig description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmtpConfig)) {
            return false;
        }
        return id != null && id.equals(((SmtpConfig) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SmtpConfig{" +
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
