package com.africom.correspmanage.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;

/**
 * A HistAction.
 */
@Entity
@Table(name = "hist_action")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "date_transaction")
    private ZonedDateTime dateTransaction;

    @Column(name = "login_user")
    private String loginUser;

    @Column(name = "transaction")
    private String transaction;

    @Column(name = "action_ref")
    private String actionRef;

    @Column(name = "action_json")
    private String actionJson;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HistAction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDateTransaction() {
        return this.dateTransaction;
    }

    public HistAction dateTransaction(ZonedDateTime dateTransaction) {
        this.setDateTransaction(dateTransaction);
        return this;
    }

    public void setDateTransaction(ZonedDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public String getLoginUser() {
        return this.loginUser;
    }

    public HistAction loginUser(String loginUser) {
        this.setLoginUser(loginUser);
        return this;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getTransaction() {
        return this.transaction;
    }

    public HistAction transaction(String transaction) {
        this.setTransaction(transaction);
        return this;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getActionRef() {
        return this.actionRef;
    }

    public HistAction actionRef(String actionRef) {
        this.setActionRef(actionRef);
        return this;
    }

    public void setActionRef(String actionRef) {
        this.actionRef = actionRef;
    }

    public String getActionJson() {
        return this.actionJson;
    }

    public HistAction actionJson(String actionJson) {
        this.setActionJson(actionJson);
        return this;
    }

    public void setActionJson(String actionJson) {
        this.actionJson = actionJson;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistAction)) {
            return false;
        }
        return id != null && id.equals(((HistAction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistAction{" +
            "id=" + getId() +
            ", dateTransaction='" + getDateTransaction() + "'" +
            ", loginUser='" + getLoginUser() + "'" +
            ", transaction='" + getTransaction() + "'" +
            ", actionRef='" + getActionRef() + "'" +
            ", actionJson='" + getActionJson() + "'" +
            "}";
    }
}
