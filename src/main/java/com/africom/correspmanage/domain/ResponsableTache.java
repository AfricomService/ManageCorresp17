package com.africom.correspmanage.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A ResponsableTache.
 */
@Entity
@Table(name = "responsable_tache")
public class ResponsableTache implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "tache_id")
    private Long tacheId;

    @Column(name = "code_dept")
    private String codeDept;

    @Column(name = "matricule_pers")
    private String matriculePers;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ResponsableTache id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTacheId() {
        return this.tacheId;
    }

    public ResponsableTache tacheId(Long tacheId) {
        this.setTacheId(tacheId);
        return this;
    }

    public void setTacheId(Long tacheId) {
        this.tacheId = tacheId;
    }

    public String getCodeDept() {
        return this.codeDept;
    }

    public ResponsableTache codeDept(String codeDept) {
        this.setCodeDept(codeDept);
        return this;
    }

    public void setCodeDept(String codeDept) {
        this.codeDept = codeDept;
    }

    public String getMatriculePers() {
        return this.matriculePers;
    }

    public ResponsableTache matriculePers(String matriculePers) {
        this.setMatriculePers(matriculePers);
        return this;
    }

    public void setMatriculePers(String matriculePers) {
        this.matriculePers = matriculePers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponsableTache)) {
            return false;
        }
        return id != null && id.equals(((ResponsableTache) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResponsableTache{" +
            "id=" + getId() +
            ", tacheId=" + getTacheId() +
            ", codeDept='" + getCodeDept() + "'" +
            ", matriculePers='" + getMatriculePers() + "'" +
            "}";
    }
}
