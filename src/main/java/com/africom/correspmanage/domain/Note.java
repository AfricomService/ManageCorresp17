package com.africom.correspmanage.domain;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Note.
 */
@Entity
@Table(name = "note")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "idinstance", nullable = false)
    private UUID idinstance;

    @Column(name = "reference")
    private String reference;

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotNull
    @Column(name = "date_created", nullable = false)
    private ZonedDateTime dateCreated;

    @NotNull
    @Column(name = "date_publication", nullable = false)
    private ZonedDateTime datePublication;

    @NotNull
    @Column(name = "date_end_publication", nullable = false)
    private ZonedDateTime dateEndPublication;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CorrespStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @JsonIgnoreProperties(value = { "corresps" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Pj attachment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Note id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getIdinstance() {
        return this.idinstance;
    }

    public Note idinstance(UUID idinstance) {
        this.setIdinstance(idinstance);
        return this;
    }

    public void setIdinstance(UUID idinstance) {
        this.idinstance = idinstance;
    }

    public String getReference() {
        return this.reference;
    }

    public Note reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSubject() {
        return this.subject;
    }

    public Note subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ZonedDateTime getDateCreated() {
        return this.dateCreated;
    }

    public Note dateCreated(ZonedDateTime dateCreated) {
        this.setDateCreated(dateCreated);
        return this;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ZonedDateTime getDatePublication() {
        return this.datePublication;
    }

    public Note datePublication(ZonedDateTime datePublication) {
        this.setDatePublication(datePublication);
        return this;
    }

    public void setDatePublication(ZonedDateTime datePublication) {
        this.datePublication = datePublication;
    }

    public ZonedDateTime getDateEndPublication() {
        return this.dateEndPublication;
    }

    public Note dateEndPublication(ZonedDateTime dateEndPublication) {
        this.setDateEndPublication(dateEndPublication);
        return this;
    }

    public void setDateEndPublication(ZonedDateTime dateEndPublication) {
        this.dateEndPublication = dateEndPublication;
    }

    public CorrespStatus getStatus() {
        return this.status;
    }

    public Note status(CorrespStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CorrespStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Note createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Note updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Note updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Pj getAttachment() {
        return this.attachment;
    }

    public void setAttachment(Pj pj) {
        this.attachment = pj;
    }

    public Note attachment(Pj pj) {
        this.setAttachment(pj);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Note)) {
            return false;
        }
        return id != null && id.equals(((Note) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Note{" +
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
            "}";
    }
}
