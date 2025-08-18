package com.africom.correspmanage.service.dto;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.Note} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NoteDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID idinstance;

    private String reference;

    @NotNull
    private String subject;

    @NotNull
    private ZonedDateTime dateCreated;

    @NotNull
    private ZonedDateTime datePublication;

    @NotNull
    private ZonedDateTime dateEndPublication;

    @NotNull
    private CorrespStatus status;

    private String createdBy;

    private String updatedBy;

    private ZonedDateTime updatedDate;

    private PjDTO attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getIdinstance() {
        return idinstance;
    }

    public void setIdinstance(UUID idinstance) {
        this.idinstance = idinstance;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ZonedDateTime getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(ZonedDateTime datePublication) {
        this.datePublication = datePublication;
    }

    public ZonedDateTime getDateEndPublication() {
        return dateEndPublication;
    }

    public void setDateEndPublication(ZonedDateTime dateEndPublication) {
        this.dateEndPublication = dateEndPublication;
    }

    public CorrespStatus getStatus() {
        return status;
    }

    public void setStatus(CorrespStatus status) {
        this.status = status;
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

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public PjDTO getAttachment() {
        return attachment;
    }

    public void setAttachment(PjDTO attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NoteDTO)) {
            return false;
        }

        NoteDTO noteDTO = (NoteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, noteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NoteDTO{" +
            "id=" + getId() +
            ", idinstance='" + getIdinstance() + "'" +
            ", reference='" + getReference() + "'" +
            ", subject='" + getSubject() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            ", datePublication='" + getDatePublication() + "'" +
            ", dateEndPublication='" + getDateEndPublication() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", attachment=" + getAttachment() +
            "}";
    }
}
