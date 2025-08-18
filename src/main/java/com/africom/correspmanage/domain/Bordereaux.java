package com.africom.correspmanage.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Bordereaux.
 */
@Entity
@Table(name = "bordereaux")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bordereaux implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @NotNull
    @Column(name = "issued_date", nullable = false)
    private ZonedDateTime issuedDate;

    @Column(name = "due_date")
    private ZonedDateTime dueDate;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToMany(mappedBy = "bordereaux")
    @JsonIgnoreProperties(value = { "dossiers", "bordereaux", "attachments", "tasks" }, allowSetters = true)
    private Set<Corresp> corresps = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bordereaux id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Bordereaux reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return this.description;
    }

    public Bordereaux description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Bordereaux amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Bordereaux currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return this.status;
    }

    public Bordereaux status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getIssuedDate() {
        return this.issuedDate;
    }

    public Bordereaux issuedDate(ZonedDateTime issuedDate) {
        this.setIssuedDate(issuedDate);
        return this;
    }

    public void setIssuedDate(ZonedDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public ZonedDateTime getDueDate() {
        return this.dueDate;
    }

    public Bordereaux dueDate(ZonedDateTime dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    public Bordereaux createdDate(ZonedDateTime createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Bordereaux updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Bordereaux createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Bordereaux updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Set<Corresp> getCorresps() {
        return this.corresps;
    }

    public void setCorresps(Set<Corresp> corresps) {
        if (this.corresps != null) {
            this.corresps.forEach(i -> i.removeBordereaux(this));
        }
        if (corresps != null) {
            corresps.forEach(i -> i.addBordereaux(this));
        }
        this.corresps = corresps;
    }

    public Bordereaux corresps(Set<Corresp> corresps) {
        this.setCorresps(corresps);
        return this;
    }

    public Bordereaux addCorresps(Corresp corresp) {
        this.corresps.add(corresp);
        corresp.getBordereaux().add(this);
        return this;
    }

    public Bordereaux removeCorresps(Corresp corresp) {
        this.corresps.remove(corresp);
        corresp.getBordereaux().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bordereaux)) {
            return false;
        }
        return id != null && id.equals(((Bordereaux) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bordereaux{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", status='" + getStatus() + "'" +
            ", issuedDate='" + getIssuedDate() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            "}";
    }
}
