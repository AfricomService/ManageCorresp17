package com.africom.correspmanage.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.CustomSearch} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomSearchDTO implements Serializable {

    private Long id;

    private ZonedDateTime dateCreation;

    private String readAccess;

    private String visibility;

    private String request;

    private String entitySearching;

    private String userLogin;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(ZonedDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getReadAccess() {
        return readAccess;
    }

    public void setReadAccess(String readAccess) {
        this.readAccess = readAccess;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getEntitySearching() {
        return entitySearching;
    }

    public void setEntitySearching(String entitySearching) {
        this.entitySearching = entitySearching;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomSearchDTO)) {
            return false;
        }

        CustomSearchDTO customSearchDTO = (CustomSearchDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customSearchDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomSearchDTO{" +
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
