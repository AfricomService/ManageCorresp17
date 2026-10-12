package com.africom.correspmanage.service.dto;

public class CorrespSummaryDTO {
    private String subject;
    private String reference;
    private String nomSociete;
    private String sender;
    private String labelDestinataire;

    public CorrespSummaryDTO() {}

    // Ancien constructeur conservé : le code existant qui l'appelle continue de compiler
    public CorrespSummaryDTO(String subject, String reference, String nomSociete) {
        this.subject = subject;
        this.reference = reference;
        this.nomSociete = nomSociete;
    }

    public CorrespSummaryDTO(String subject, String reference, String nomSociete, String sender, String labelDestinataire) {
        this.subject = subject;
        this.reference = reference;
        this.nomSociete = nomSociete;
        this.sender = sender;
        this.labelDestinataire = labelDestinataire;
    }

    public String getSubject()              { return subject; }
    public void setSubject(String subject)  { this.subject = subject; }
    public String getReference()            { return reference; }
    public void setReference(String ref)    { this.reference = ref; }
    public String getNomSociete()           { return nomSociete; }
    public void setNomSociete(String nom)   { this.nomSociete = nom; }
    public String getSender()               { return sender; }
    public void setSender(String sender)    { this.sender = sender; }
    public String getLabelDestinataire()    { return labelDestinataire; }
    public void setLabelDestinataire(String l) { this.labelDestinataire = l; }
}
