package com.africom.correspmanage.service.dto;

public class CorrespSummaryDTO {
    private String subject;
    private String reference;

    public CorrespSummaryDTO() {}

    public CorrespSummaryDTO(String subject, String reference) {
        this.subject = subject;
        this.reference = reference;
    }

    public String getSubject()              { return subject; }
    public void setSubject(String subject)  { this.subject = subject; }
    public String getReference()            { return reference; }
    public void setReference(String ref)    { this.reference = ref; }
}
