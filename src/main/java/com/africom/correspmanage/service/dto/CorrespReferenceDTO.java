package com.africom.correspmanage.service.dto;

import java.time.ZonedDateTime;

public class CorrespReferenceDTO {
    private Long id;
    private String reference;
    private String subject;
    private ZonedDateTime dateCreated;
    private String sender;

    public CorrespReferenceDTO() {}

    public CorrespReferenceDTO(Long id, String reference, String subject,
                               ZonedDateTime dateCreated, String sender) {
        this.id = id;
        this.reference = reference;
        this.subject = subject;
        this.dateCreated = dateCreated;
        this.sender = sender;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public ZonedDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(ZonedDateTime dateCreated) { this.dateCreated = dateCreated; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
}
