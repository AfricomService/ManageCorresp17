package com.africom.correspmanage.service.dto;

import java.util.List;
import java.util.UUID;

public class SendCorrespEmailRequestDTO {

    private Long   correspId;
    private UUID   modelNotifId;
    private String receiver;
    private String copyTo;
    private String correspBody;
    private String senderFullName;
    private String senderDepartement;
    private String senderEmail;

    // ── Champs supplémentaires pour la preview ──────────────────
    private String correspObjet;        // objet de la correspondance
    private String correspDate;         // date formatée "dd/MM/yyyy"
    private String correspReference;    // référence (null si pas encore générée)
    private String destinataireLabel;   // nom du destinataire

    // Getters / Setters
    public String getCorrespObjet()      { return correspObjet; }
    public void setCorrespObjet(String v){ this.correspObjet = v; }

    public String getCorrespDate()       { return correspDate; }
    public void setCorrespDate(String v) { this.correspDate = v; }

    public String getCorrespReference()      { return correspReference; }
    public void setCorrespReference(String v){ this.correspReference = v; }

    public String getDestinataireLabel()      { return destinataireLabel; }
    public void setDestinataireLabel(String v){ this.destinataireLabel = v; }

    // ... (garder les getters/setters existants)
    public Long getCorrespId()            { return correspId; }
    public void setCorrespId(Long v)      { this.correspId = v; }
    public UUID getModelNotifId()         { return modelNotifId; }
    public void setModelNotifId(UUID v)   { this.modelNotifId = v; }
    public String getReceiver()           { return receiver; }
    public void setReceiver(String v)     { this.receiver = v; }
    public String getCopyTo()             { return copyTo; }
    public void setCopyTo(String v)       { this.copyTo = v; }
    public String getCorrespBody()        { return correspBody; }
    public void setCorrespBody(String v)  { this.correspBody = v; }
    public String getSenderFullName()     { return senderFullName; }
    public void setSenderFullName(String v){ this.senderFullName = v; }
    public String getSenderDepartement()  { return senderDepartement; }
    public void setSenderDepartement(String v){ this.senderDepartement = v; }
    public String getSenderEmail()        { return senderEmail; }
    public void setSenderEmail(String v)  { this.senderEmail = v; }
}
