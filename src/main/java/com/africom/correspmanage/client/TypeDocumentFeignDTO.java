package com.africom.correspmanage.client;

import java.io.Serializable;
import java.util.Objects;

/**
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypeDocumentFeignDTO implements Serializable {

    private Long id;

    private String nom;

    private String defautdest;

    private String defaultobject;

    private String defaultencopie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDefautdest() {
        return defautdest;
    }

    public void setDefautdest(String defautdest) {
        this.defautdest = defautdest;
    }

    public String getDefaultobject() {
        return defaultobject;
    }

    public void setDefaultobject(String defaultobject) {
        this.defaultobject = defaultobject;
    }

    public String getDefaultencopie() {
        return defaultencopie;
    }

    public void setDefaultencopie(String defaultencopie) {
        this.defaultencopie = defaultencopie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeDocumentFeignDTO)) {
            return false;
        }

        TypeDocumentFeignDTO typeDocumentFeignDTO = (TypeDocumentFeignDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, typeDocumentFeignDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypeDocumentFeignDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", defautdest='" + getDefautdest() + "'" +
            ", defaultobject='" + getDefaultobject() + "'" +
            ", defaultencopie='" + getDefaultencopie() + "'" +
            "}";
    }
}
