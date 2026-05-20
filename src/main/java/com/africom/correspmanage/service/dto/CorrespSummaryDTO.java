package com.africom.correspmanage.service.dto;

public class CorrespSummaryDTO {
    private String subject;
    private String reference;
    private String nomSociete;

    public CorrespSummaryDTO() {}

    public CorrespSummaryDTO(String subject, String reference, String nomSociete) {
        this.subject = subject;
        this.reference = reference;
        this.nomSociete = nomSociete;
    }

    public String getSubject()              { return subject; }
    public void setSubject(String subject)  { this.subject = subject; }
    public String getReference()            { return reference; }
    public void setReference(String ref)    { this.reference = ref; }
    public String getNomSociete()           { return nomSociete; }
    public void setNomSociete(String nom)   { this.nomSociete = nom; }
}
