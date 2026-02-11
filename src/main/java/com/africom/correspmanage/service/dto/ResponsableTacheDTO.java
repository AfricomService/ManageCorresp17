package com.africom.correspmanage.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.ResponsableTache} entity.
 */
public class ResponsableTacheDTO implements Serializable {

    private Long id;

    private Long tacheId;

    private String codeDept;

    private String matriculePers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTacheId() {
        return tacheId;
    }

    public void setTacheId(Long tacheId) {
        this.tacheId = tacheId;
    }

    public String getCodeDept() {
        return codeDept;
    }

    public void setCodeDept(String codeDept) {
        this.codeDept = codeDept;
    }

    public String getMatriculePers() {
        return matriculePers;
    }

    public void setMatriculePers(String matriculePers) {
        this.matriculePers = matriculePers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponsableTacheDTO)) {
            return false;
        }

        ResponsableTacheDTO responsableTacheDTO = (ResponsableTacheDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, responsableTacheDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResponsableTacheDTO{" +
            "id=" + getId() +
            ", tacheId=" + getTacheId() +
            ", codeDept='" + getCodeDept() + "'" +
            ", matriculePers='" + getMatriculePers() + "'" +
            "}";
    }
}
