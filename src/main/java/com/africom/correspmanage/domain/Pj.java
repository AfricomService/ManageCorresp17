package com.africom.correspmanage.domain;

import com.africom.correspmanage.domain.enumeration.UsualStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Pj.
 */
@Entity
@Table(name = "pj")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Pj implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "path")
    private String path;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "password")
    private String password;

    @Column(name = "transferable")
    private Boolean transferable;

    @Column(name = "unique_name")
    private String uniqueName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UsualStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @ManyToMany(mappedBy = "attachments")
    @JsonIgnoreProperties(value = { "dossiers", "bordereaux", "attachments", "tasks" }, allowSetters = true)
    private Set<Corresp> corresps = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pj id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Pj name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileType() {
        return this.fileType;
    }

    public Pj fileType(String fileType) {
        this.setFileType(fileType);
        return this;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return this.path;
    }

    public Pj path(String path) {
        this.setPath(path);
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public Pj creationDate(ZonedDateTime creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getPassword() {
        return this.password;
    }

    public Pj password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getTransferable() {
        return this.transferable;
    }

    public Pj transferable(Boolean transferable) {
        this.setTransferable(transferable);
        return this;
    }

    public void setTransferable(Boolean transferable) {
        this.transferable = transferable;
    }

    public String getUniqueName() {
        return this.uniqueName;
    }

    public Pj uniqueName(String uniqueName) {
        this.setUniqueName(uniqueName);
        return this;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public UsualStatus getStatus() {
        return this.status;
    }

    public Pj status(UsualStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(UsualStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Pj createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Pj updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Pj updatedDate(ZonedDateTime updatedDate) {
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
            this.corresps.forEach(i -> i.removeAttachments(this));
        }
        if (corresps != null) {
            corresps.forEach(i -> i.addAttachments(this));
        }
        this.corresps = corresps;
    }

    public Pj corresps(Set<Corresp> corresps) {
        this.setCorresps(corresps);
        return this;
    }

    public Pj addCorresps(Corresp corresp) {
        this.corresps.add(corresp);
        corresp.getAttachments().add(this);
        return this;
    }

    public Pj removeCorresps(Corresp corresp) {
        this.corresps.remove(corresp);
        corresp.getAttachments().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pj)) {
            return false;
        }
        return id != null && id.equals(((Pj) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pj{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", path='" + getPath() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", password='" + getPassword() + "'" +
            ", transferable='" + getTransferable() + "'" +
            ", uniqueName='" + getUniqueName() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            "}";
    }
}
