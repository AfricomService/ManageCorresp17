package com.africom.correspmanage.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;

/**
 * A CustomSearch.
 */
@Entity
@Table(name = "custom_search")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "date_creation")
    private ZonedDateTime dateCreation;

    @Column(name = "read_access")
    private String readAccess;

    @Column(name = "visibility")
    private String visibility;

    @Column(name = "request")
    private String request;

    @Column(name = "entity_searching")
    private String entitySearching;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "name")
    private String name;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomSearch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateCreation() {
        return this.dateCreation;
    }

    public CustomSearch dateCreation(ZonedDateTime dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(ZonedDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getReadAccess() {
        return this.readAccess;
    }

    public CustomSearch readAccess(String readAccess) {
        this.setReadAccess(readAccess);
        return this;
    }

    public void setReadAccess(String readAccess) {
        this.readAccess = readAccess;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public CustomSearch visibility(String visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getRequest() {
        return this.request;
    }

    public CustomSearch request(String request) {
        this.setRequest(request);
        return this;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getEntitySearching() {
        return this.entitySearching;
    }

    public CustomSearch entitySearching(String entitySearching) {
        this.setEntitySearching(entitySearching);
        return this;
    }

    public void setEntitySearching(String entitySearching) {
        this.entitySearching = entitySearching;
    }

    public String getUserLogin() {
        return this.userLogin;
    }

    public CustomSearch userLogin(String userLogin) {
        this.setUserLogin(userLogin);
        return this;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getName() {
        return this.name;
    }

    public CustomSearch name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomSearch)) {
            return false;
        }
        return id != null && id.equals(((CustomSearch) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomSearch{" +
            "id=" + getId() +
            ", dateCreation='" + getDateCreation() + "'" +
            ", readAccess='" + getReadAccess() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", request='" + getRequest() + "'" +
            ", entitySearching='" + getEntitySearching() + "'" +
            ", userLogin='" + getUserLogin() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
