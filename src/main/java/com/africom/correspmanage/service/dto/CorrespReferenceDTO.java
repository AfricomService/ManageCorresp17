package com.africom.correspmanage.service.dto;

public class CorrespReferenceDTO {
    private Long id;
    private String reference;
    private String subject;

    public CorrespReferenceDTO() {
    }

    public CorrespReferenceDTO(Long id, String reference, String subject) {
        this.id = id;
        this.reference = reference;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
