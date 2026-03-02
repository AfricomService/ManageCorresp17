package com.africom.correspmanage.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A ContactCopieCorresp.
 */
@Entity
@Table(name = "contact_copie_corresp")
public class ContactCopieCorresp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "id_corrsp")
    private Long idCorrsp;

    @Column(name = "id_dept")
    private Long idDept;

    @Column(name = "code_dept")
    private String codeDept;

    @Column(name = "nom_dept")
    private String nomDept;

    @Column(name = "email_dept")
    private String emailDept;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ContactCopieCorresp id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCorrsp() {
        return this.idCorrsp;
    }

    public ContactCopieCorresp idCorrsp(Long idCorrsp) {
        this.setIdCorrsp(idCorrsp);
        return this;
    }

    public void setIdCorrsp(Long idCorrsp) {
        this.idCorrsp = idCorrsp;
    }

    public Long getIdDept() {
        return this.idDept;
    }

    public ContactCopieCorresp idDept(Long idDept) {
        this.setIdDept(idDept);
        return this;
    }

    public void setIdDept(Long idDept) {
        this.idDept = idDept;
    }

    public String getCodeDept() {
        return this.codeDept;
    }

    public ContactCopieCorresp codeDept(String codeDept) {
        this.setCodeDept(codeDept);
        return this;
    }

    public void setCodeDept(String codeDept) {
        this.codeDept = codeDept;
    }

    public String getNomDept() {
        return this.nomDept;
    }

    public ContactCopieCorresp nomDept(String nomDept) {
        this.setNomDept(nomDept);
        return this;
    }

    public void setNomDept(String nomDept) {
        this.nomDept = nomDept;
    }

    public String getEmailDept() {
        return this.emailDept;
    }

    public ContactCopieCorresp emailDept(String emailDept) {
        this.setEmailDept(emailDept);
        return this;
    }

    public void setEmailDept(String emailDept) {
        this.emailDept = emailDept;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContactCopieCorresp)) {
            return false;
        }
        return id != null && id.equals(((ContactCopieCorresp) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContactCopieCorresp{" +
            "id=" + getId() +
            ", idCorrsp=" + getIdCorrsp() +
            ", idDept=" + getIdDept() +
            ", codeDept='" + getCodeDept() + "'" +
            ", nomDept='" + getNomDept() + "'" +
            ", emailDept='" + getEmailDept() + "'" +
            "}";
    }
}
