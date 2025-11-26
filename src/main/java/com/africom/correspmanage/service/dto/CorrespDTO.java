package com.africom.correspmanage.service.dto;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.domain.enumeration.CorrespType;
import com.africom.correspmanage.domain.enumeration.DispatchType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.Corresp} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CorrespDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID idinstance;

    private String reference;

    @NotNull
    private String subject;

    @NotNull
    private ZonedDateTime dateCreated;

    @NotNull
    private ZonedDateTime courrierDate;

    @NotNull
    private CorrespStatus status;

    private Boolean priority;

    @Lob
    private String body;

    @NotNull
    private CorrespType corresptype;

    @NotNull
    private String sender;

    private Boolean needsResponse;

    private String responseTo;

    private String step;

    private String stepCode;

    private Long processId;

    private String processVersion;

    private String contact;

    private String senderReference;

    @NotNull
    private DispatchType dispatchType;

    private String contactCompany;

    private String personCompany;

    private String contactAddress;

    private String contactEmail;

    private String contactEmailCc;

    private String createdBy;

    private String updatedBy;

    private ZonedDateTime updatedDate;

    private Long typeDocumentId;

    private String labelDestinataire;

    private String labelContactCC;

    private Set<DossierDTO> dossiers = new HashSet<>();

    private Set<BordereauxDTO> bordereaux = new HashSet<>();

    private Set<PjDTO> attachments = new HashSet<>();

    private Set<TasksDTO> tasks = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getIdinstance() {
        return idinstance;
    }

    public void setIdinstance(UUID idinstance) {
        this.idinstance = idinstance;
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

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ZonedDateTime getCourrierDate() {
        return courrierDate;
    }

    public void setCourrierDate(ZonedDateTime courrierDate) {
        this.courrierDate = courrierDate;
    }

    public CorrespStatus getStatus() {
        return status;
    }

    public void setStatus(CorrespStatus status) {
        this.status = status;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public CorrespType getCorresptype() {
        return corresptype;
    }

    public void setCorresptype(CorrespType corresptype) {
        this.corresptype = corresptype;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Boolean getNeedsResponse() {
        return needsResponse;
    }

    public void setNeedsResponse(Boolean needsResponse) {
        this.needsResponse = needsResponse;
    }

    public String getResponseTo() {
        return responseTo;
    }

    public void setResponseTo(String responseTo) {
        this.responseTo = responseTo;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getStepCode() {
        return stepCode;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSenderReference() {
        return senderReference;
    }

    public void setSenderReference(String senderReference) {
        this.senderReference = senderReference;
    }

    public DispatchType getDispatchType() {
        return dispatchType;
    }

    public void setDispatchType(DispatchType dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getContactCompany() {
        return contactCompany;
    }

    public void setContactCompany(String contactCompany) {
        this.contactCompany = contactCompany;
    }

    public String getPersonCompany() {
        return personCompany;
    }

    public void setPersonCompany(String personCompany) {
        this.personCompany = personCompany;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactEmailCc() {
        return contactEmailCc;
    }

    public void setContactEmailCc(String contactEmailCc) {
        this.contactEmailCc = contactEmailCc;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getTypeDocumentId() {
        return typeDocumentId;
    }

    public void setTypeDocumentId(Long typeDocumentId) {
        this.typeDocumentId = typeDocumentId;
    }

    public String getLabelDestinataire() {
        return labelDestinataire;
    }

    public void setLabelDestinataire(String labelDestinataire) {
        this.labelDestinataire = labelDestinataire;
    }

    public String getLabelContactCC() {
        return labelContactCC;
    }

    public void setLabelContactCC(String labelContactCC) {
        this.labelContactCC = labelContactCC;
    }

    public Set<DossierDTO> getDossiers() {
        return dossiers;
    }

    public void setDossiers(Set<DossierDTO> dossiers) {
        this.dossiers = dossiers;
    }

    public Set<BordereauxDTO> getBordereaux() {
        return bordereaux;
    }

    public void setBordereaux(Set<BordereauxDTO> bordereaux) {
        this.bordereaux = bordereaux;
    }

    public Set<PjDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<PjDTO> attachments) {
        this.attachments = attachments;
    }

    public Set<TasksDTO> getTasks() {
        return tasks;
    }

    public void setTasks(Set<TasksDTO> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CorrespDTO)) {
            return false;
        }

        CorrespDTO correspDTO = (CorrespDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, correspDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CorrespDTO{" +
            "id=" + getId() +
            ", idinstance='" + getIdinstance() + "'" +
            ", reference='" + getReference() + "'" +
            ", subject='" + getSubject() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            ", courrierDate='" + getCourrierDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", priority='" + getPriority() + "'" +
            ", body='" + getBody() + "'" +
            ", corresptype='" + getCorresptype() + "'" +
            ", sender='" + getSender() + "'" +
            ", needsResponse='" + getNeedsResponse() + "'" +
            ", responseTo='" + getResponseTo() + "'" +
            ", step='" + getStep() + "'" +
            ", stepCode='" + getStepCode() + "'" +
            ", processId=" + getProcessId() +
            ", processVersion='" + getProcessVersion() + "'" +
            ", contact='" + getContact() + "'" +
            ", senderReference='" + getSenderReference() + "'" +
            ", dispatchType='" + getDispatchType() + "'" +
            ", contactCompany='" + getContactCompany() + "'" +
            ", personCompany='" + getPersonCompany() + "'" +
            ", contactAddress='" + getContactAddress() + "'" +
            ", contactEmail='" + getContactEmail() + "'" +
            ", contactEmailCc='" + getContactEmailCc() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", typeDocumentId=" + getTypeDocumentId() +
            ", labelDestinataire='" + getLabelDestinataire() + "'" +
            ", labelContactCC='" + getLabelContactCC() + "'" +
            ", dossiers=" + getDossiers() +
            ", bordereaux=" + getBordereaux() +
            ", attachments=" + getAttachments() +
            ", tasks=" + getTasks() +
            "}";
    }
}
