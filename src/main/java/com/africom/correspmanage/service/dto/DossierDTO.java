package com.africom.correspmanage.service.dto;

import com.africom.correspmanage.domain.enumeration.UsualStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.Dossier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DossierDTO implements Serializable {

    private Long id;

    private String name;

    private Boolean isPublic;

    private String responsible;

    private UsualStatus status;

    @Lob
    private byte[] dossierList;

    private String dossierListContentType;
    private String createdBy;

    private String updatedBy;

    private ZonedDateTime updatedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public UsualStatus getStatus() {
        return status;
    }

    public void setStatus(UsualStatus status) {
        this.status = status;
    }

    public byte[] getDossierList() {
        return dossierList;
    }

    public void setDossierList(byte[] dossierList) {
        this.dossierList = dossierList;
    }

    public String getDossierListContentType() {
        return dossierListContentType;
    }

    public void setDossierListContentType(String dossierListContentType) {
        this.dossierListContentType = dossierListContentType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DossierDTO)) {
            return false;
        }

        DossierDTO dossierDTO = (DossierDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dossierDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DossierDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            ", responsible='" + getResponsible() + "'" +
            ", status='" + getStatus() + "'" +
            ", dossierList='" + getDossierList() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            "}";
    }
}
