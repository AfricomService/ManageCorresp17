package com.africom.correspmanage.domain;

import com.africom.correspmanage.domain.enumeration.UsualStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Dossier.
 */
@Entity
@Table(name = "dossier")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dossier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "responsible")
    private String responsible;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UsualStatus status;

    @Lob
    @Column(name = "dossier_list")
    private byte[] dossierList;

    @Column(name = "dossier_list_content_type")
    private String dossierListContentType;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @ManyToMany(mappedBy = "dossiers")
    @JsonIgnoreProperties(value = { "dossiers", "bordereaux", "attachments", "tasks" }, allowSetters = true)
    private Set<Corresp> corresps = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dossier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Dossier name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public Dossier isPublic(Boolean isPublic) {
        this.setIsPublic(isPublic);
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getResponsible() {
        return this.responsible;
    }

    public Dossier responsible(String responsible) {
        this.setResponsible(responsible);
        return this;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public UsualStatus getStatus() {
        return this.status;
    }

    public Dossier status(UsualStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(UsualStatus status) {
        this.status = status;
    }

    public byte[] getDossierList() {
        return this.dossierList;
    }

    public Dossier dossierList(byte[] dossierList) {
        this.setDossierList(dossierList);
        return this;
    }

    public void setDossierList(byte[] dossierList) {
        this.dossierList = dossierList;
    }

    public String getDossierListContentType() {
        return this.dossierListContentType;
    }

    public Dossier dossierListContentType(String dossierListContentType) {
        this.dossierListContentType = dossierListContentType;
        return this;
    }

    public void setDossierListContentType(String dossierListContentType) {
        this.dossierListContentType = dossierListContentType;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Dossier createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Dossier updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Dossier updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Set<Corresp> getCorresps() {
        return this.corresps;
    }

    public void setCorresps(Set<Corresp> corresps) {
        if (this.corresps != null) {
            this.corresps.forEach(i -> i.removeDossiers(this));
        }
        if (corresps != null) {
            corresps.forEach(i -> i.addDossiers(this));
        }
        this.corresps = corresps;
    }

    public Dossier corresps(Set<Corresp> corresps) {
        this.setCorresps(corresps);
        return this;
    }

    public Dossier addCorresps(Corresp corresp) {
        this.corresps.add(corresp);
        corresp.getDossiers().add(this);
        return this;
    }

    public Dossier removeCorresps(Corresp corresp) {
        this.corresps.remove(corresp);
        corresp.getDossiers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dossier)) {
            return false;
        }
        return id != null && id.equals(((Dossier) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dossier{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            ", responsible='" + getResponsible() + "'" +
            ", status='" + getStatus() + "'" +
            ", dossierList='" + getDossierList() + "'" +
            ", dossierListContentType='" + getDossierListContentType() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            "}";
    }
}
