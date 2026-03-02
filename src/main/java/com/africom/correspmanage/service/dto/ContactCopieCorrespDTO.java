package com.africom.correspmanage.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.ContactCopieCorresp} entity.
 */
public class ContactCopieCorrespDTO implements Serializable {

    private Long id;

    private Long idCorrsp;

    private Long idDept;

    private String codeDept;

    private String nomDept;

    private String emailDept;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCorrsp() {
        return idCorrsp;
    }

    public void setIdCorrsp(Long idCorrsp) {
        this.idCorrsp = idCorrsp;
    }

    public Long getIdDept() {
        return idDept;
    }

    public void setIdDept(Long idDept) {
        this.idDept = idDept;
    }

    public String getCodeDept() {
        return codeDept;
    }

    public void setCodeDept(String codeDept) {
        this.codeDept = codeDept;
    }

    public String getNomDept() {
        return nomDept;
    }

    public void setNomDept(String nomDept) {
        this.nomDept = nomDept;
    }

    public String getEmailDept() {
        return emailDept;
    }

    public void setEmailDept(String emailDept) {
        this.emailDept = emailDept;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactCopieCorrespDTO)) {
            return false;
        }

        ContactCopieCorrespDTO contactCopieCorrespDTO = (ContactCopieCorrespDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, contactCopieCorrespDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContactCopieCorrespDTO{" +
            "id=" + getId() +
            ", idCorrsp=" + getIdCorrsp() +
            ", idDept=" + getIdDept() +
            ", codeDept='" + getCodeDept() + "'" +
            ", nomDept='" + getNomDept() + "'" +
            ", emailDept='" + getEmailDept() + "'" +
            "}";
    }
}
