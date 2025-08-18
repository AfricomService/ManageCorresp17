package com.africom.correspmanage.service.dto;

import com.africom.correspmanage.domain.enumeration.UsualStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.Pj} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PjDTO implements Serializable {

    private Long id;

    private String name;

    private String fileType;

    private String path;

    private ZonedDateTime creationDate;

    private String password;

    private Boolean transferable;

    private String uniqueName;

    private UsualStatus status;

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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getTransferable() {
        return transferable;
    }

    public void setTransferable(Boolean transferable) {
        this.transferable = transferable;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public UsualStatus getStatus() {
        return status;
    }

    public void setStatus(UsualStatus status) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PjDTO)) {
            return false;
        }

        PjDTO pjDTO = (PjDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pjDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PjDTO{" +
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
