package com.africom.correspmanage.service.dto;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.domain.enumeration.CorrespType;
import com.africom.correspmanage.domain.enumeration.DispatchType;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * DTO léger pour l'affichage de la liste des correspondances.
 * Inclut sender et receiver depuis l'entité Message (messageType = COURRIER).
 */
public class CorrespListItemDTO implements Serializable {

    private Long id;
    private String reference;
    private CorrespType corresptype;
    private ZonedDateTime courrierDate;
    private String subject;       // gardé pour la recherche / affichage secondaire
    private CorrespStatus status;
    private Boolean priority;

    // Depuis l'entité Message (messageType = COURRIER, correspId = corresp.id)
    private String msgSender;     // message.sender
    private String msgReceiver;   // message.receiver (liste JSON → on concatène les emails)
    private DispatchType dispatchType;

    private String nomSociete;

    public CorrespListItemDTO() {}

    public CorrespListItemDTO(
        Long id,
        String reference,
        CorrespType corresptype,
        ZonedDateTime courrierDate,
        String subject,
        CorrespStatus status,
        Boolean priority,
        String msgSender,
        String msgReceiver
    ) {
        this.id = id;
        this.reference = reference;
        this.corresptype = corresptype;
        this.courrierDate = courrierDate;
        this.subject = subject;
        this.status = status;
        this.priority = priority;
        this.msgSender = msgSender;
        this.msgReceiver = msgReceiver;
    }

    // --- Getters / Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public CorrespType getCorresptype() { return corresptype; }
    public void setCorresptype(CorrespType corresptype) { this.corresptype = corresptype; }

    public ZonedDateTime getCourrierDate() { return courrierDate; }
    public void setCourrierDate(ZonedDateTime courrierDate) { this.courrierDate = courrierDate; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public CorrespStatus getStatus() { return status; }
    public void setStatus(CorrespStatus status) { this.status = status; }

    public Boolean getPriority() { return priority; }
    public void setPriority(Boolean priority) { this.priority = priority; }

    public String getMsgSender() { return msgSender; }
    public void setMsgSender(String msgSender) { this.msgSender = msgSender; }

    public String getMsgReceiver() { return msgReceiver; }
    public void setMsgReceiver(String msgReceiver) { this.msgReceiver = msgReceiver; }

    public DispatchType getDispatchType() { return dispatchType; }
    public void setDispatchType(DispatchType dispatchType) { this.dispatchType = dispatchType; }

    public String getNomSociete() { return nomSociete; }
    public void setNomSociete(String nomSociete) { this.nomSociete = nomSociete; }
}
