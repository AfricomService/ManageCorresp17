package com.africom.correspmanage.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.Bordereaux} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BordereauxDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    private String description;

    @NotNull
    private BigDecimal amount;

    private String currency;

    @NotNull
    private String status;

    @NotNull
    private ZonedDateTime issuedDate;

    private ZonedDateTime dueDate;

    private ZonedDateTime createdDate;

    private ZonedDateTime updatedDate;

    private String createdBy;

    private String updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(ZonedDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BordereauxDTO)) {
            return false;
        }

        BordereauxDTO bordereauxDTO = (BordereauxDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bordereauxDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BordereauxDTO{" +
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
