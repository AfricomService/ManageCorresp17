package com.africom.correspmanage.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.HistAction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistActionDTO implements Serializable {

    private Long id;

    private ZonedDateTime dateTransaction;

    private String loginUser;

    private String transaction;

    private String actionRef;

    private String actionJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(ZonedDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getActionRef() {
        return actionRef;
    }

    public void setActionRef(String actionRef) {
        this.actionRef = actionRef;
    }

    public String getActionJson() {
        return actionJson;
    }

    public void setActionJson(String actionJson) {
        this.actionJson = actionJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistActionDTO)) {
            return false;
        }

        HistActionDTO histActionDTO = (HistActionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, histActionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistActionDTO{" +
            "id=" + getId() +
            ", dateTransaction='" + getDateTransaction() + "'" +
            ", loginUser='" + getLoginUser() + "'" +
            ", transaction='" + getTransaction() + "'" +
            ", actionRef='" + getActionRef() + "'" +
            ", actionJson='" + getActionJson() + "'" +
            "}";
    }
}
