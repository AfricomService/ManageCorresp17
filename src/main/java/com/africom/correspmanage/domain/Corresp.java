package com.africom.correspmanage.domain;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.africom.correspmanage.domain.enumeration.CorrespType;
import com.africom.correspmanage.domain.enumeration.DispatchType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * A Corresp.
 */
@Entity
@Table(name = "corresp")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Corresp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "idinstance", nullable = false)
    private UUID idinstance;

    @Column(name = "reference", unique = true)
    private String reference;

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotNull
    @Column(name = "date_created", nullable = false)
    private ZonedDateTime dateCreated;

    @NotNull
    @Column(name = "courrier_date", nullable = false)
    private ZonedDateTime courrierDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CorrespStatus status;

    @Column(name = "priority")
    private Boolean priority;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "body")
    private String body;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "corresptype", nullable = false)
    private CorrespType corresptype;

    @NotNull
    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "needs_response")
    private Boolean needsResponse;

    @Column(name = "response_to")
    private String responseTo;

    @Column(name = "step")
    private String step;

    @Column(name = "step_code")
    private String stepCode;

    @Column(name = "process_id")
    private Long processId;

    @Column(name = "process_version")
    private String processVersion;

    @Column(name = "contact")
    private String contact;

    @Column(name = "sender_reference")
    private String senderReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "dispatch_type", nullable = false)
    private DispatchType dispatchType;

    @Column(name = "contact_company")
    private String contactCompany;

    @Column(name = "person_company")
    private String personCompany;

    @Column(name = "contact_address")
    private String contactAddress;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_email_cc")
    private String contactEmailCc;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "type_document_id")
    private Long typeDocumentId;

    @Column(name = "label_destinataire")
    private String labelDestinataire;

    @Column(name = "label_contact_cc")
    private String labelContactCC;

    @Column(name = "id_societe")
    private Long idSociete;

    @Column(name = "nom_societe")
    private String nomSociete;

    @Column(name = "code_unique_societe")
    private String codeUniqueSociete;

    @ManyToMany
    @JoinTable(
        name = "rel_corresp__dossiers",
        joinColumns = @JoinColumn(name = "corresp_id"),
        inverseJoinColumns = @JoinColumn(name = "dossiers_id")
    )
    @JsonIgnoreProperties(value = { "corresps" }, allowSetters = true)
    private Set<Dossier> dossiers = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_corresp__bordereaux",
        joinColumns = @JoinColumn(name = "corresp_id"),
        inverseJoinColumns = @JoinColumn(name = "bordereaux_id")
    )
    @JsonIgnoreProperties(value = { "corresps" }, allowSetters = true)
    private Set<Bordereaux> bordereaux = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_corresp__attachments",
        joinColumns = @JoinColumn(name = "corresp_id"),
        inverseJoinColumns = @JoinColumn(name = "attachments_id")
    )
    @JsonIgnoreProperties(value = { "corresps" }, allowSetters = true)
    private Set<Pj> attachments = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_corresp__tasks",
        joinColumns = @JoinColumn(name = "corresp_id"),
        inverseJoinColumns = @JoinColumn(name = "tasks_id")
    )
    @JsonIgnoreProperties(value = { "parentDoc", "parentTask", "corresps" }, allowSetters = true)
    private Set<Tasks> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Corresp id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getIdinstance() {
        return this.idinstance;
    }

    public Corresp idinstance(UUID idinstance) {
        this.setIdinstance(idinstance);
        return this;
    }

    public void setIdinstance(UUID idinstance) {
        this.idinstance = idinstance;
    }

    public String getReference() {
        return this.reference;
    }

    public Corresp reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSubject() {
        return this.subject;
    }

    public Corresp subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ZonedDateTime getDateCreated() {
        return this.dateCreated;
    }

    public Corresp dateCreated(ZonedDateTime dateCreated) {
        this.setDateCreated(dateCreated);
        return this;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ZonedDateTime getCourrierDate() {
        return this.courrierDate;
    }

    public Corresp courrierDate(ZonedDateTime courrierDate) {
        this.setCourrierDate(courrierDate);
        return this;
    }

    public void setCourrierDate(ZonedDateTime courrierDate) {
        this.courrierDate = courrierDate;
    }

    public CorrespStatus getStatus() {
        return this.status;
    }

    public Corresp status(CorrespStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CorrespStatus status) {
        this.status = status;
    }

    public Boolean getPriority() {
        return this.priority;
    }

    public Corresp priority(Boolean priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public String getBody() {
        return this.body;
    }

    public Corresp body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public CorrespType getCorresptype() {
        return this.corresptype;
    }

    public Corresp corresptype(CorrespType corresptype) {
        this.setCorresptype(corresptype);
        return this;
    }

    public void setCorresptype(CorrespType corresptype) {
        this.corresptype = corresptype;
    }

    public String getSender() {
        return this.sender;
    }

    public Corresp sender(String sender) {
        this.setSender(sender);
        return this;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Boolean getNeedsResponse() {
        return this.needsResponse;
    }

    public Corresp needsResponse(Boolean needsResponse) {
        this.setNeedsResponse(needsResponse);
        return this;
    }

    public void setNeedsResponse(Boolean needsResponse) {
        this.needsResponse = needsResponse;
    }

    public String getResponseTo() {
        return this.responseTo;
    }

    public Corresp responseTo(String responseTo) {
        this.setResponseTo(responseTo);
        return this;
    }

    public void setResponseTo(String responseTo) {
        this.responseTo = responseTo;
    }

    public String getStep() {
        return this.step;
    }

    public Corresp step(String step) {
        this.setStep(step);
        return this;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getStepCode() {
        return this.stepCode;
    }

    public Corresp stepCode(String stepCode) {
        this.setStepCode(stepCode);
        return this;
    }

    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
    }

    public Long getProcessId() {
        return this.processId;
    }

    public Corresp processId(Long processId) {
        this.setProcessId(processId);
        return this;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessVersion() {
        return this.processVersion;
    }

    public Corresp processVersion(String processVersion) {
        this.setProcessVersion(processVersion);
        return this;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getContact() {
        return this.contact;
    }

    public Corresp contact(String contact) {
        this.setContact(contact);
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSenderReference() {
        return this.senderReference;
    }

    public Corresp senderReference(String senderReference) {
        this.setSenderReference(senderReference);
        return this;
    }

    public void setSenderReference(String senderReference) {
        this.senderReference = senderReference;
    }

    public DispatchType getDispatchType() {
        return this.dispatchType;
    }

    public Corresp dispatchType(DispatchType dispatchType) {
        this.setDispatchType(dispatchType);
        return this;
    }

    public void setDispatchType(DispatchType dispatchType) {
        this.dispatchType = dispatchType;
    }

    public String getContactCompany() {
        return this.contactCompany;
    }

    public Corresp contactCompany(String contactCompany) {
        this.setContactCompany(contactCompany);
        return this;
    }

    public void setContactCompany(String contactCompany) {
        this.contactCompany = contactCompany;
    }

    public String getPersonCompany() {
        return this.personCompany;
    }

    public Corresp personCompany(String personCompany) {
        this.setPersonCompany(personCompany);
        return this;
    }

    public void setPersonCompany(String personCompany) {
        this.personCompany = personCompany;
    }

    public String getContactAddress() {
        return this.contactAddress;
    }

    public Corresp contactAddress(String contactAddress) {
        this.setContactAddress(contactAddress);
        return this;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public Corresp contactEmail(String contactEmail) {
        this.setContactEmail(contactEmail);
        return this;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactEmailCc() {
        return this.contactEmailCc;
    }

    public Corresp contactEmailCc(String contactEmailCc) {
        this.setContactEmailCc(contactEmailCc);
        return this;
    }

    public void setContactEmailCc(String contactEmailCc) {
        this.contactEmailCc = contactEmailCc;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Corresp createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Corresp updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Corresp updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getTypeDocumentId() {
        return this.typeDocumentId;
    }

    public Corresp typeDocumentId(Long typeDocumentId) {
        this.setTypeDocumentId(typeDocumentId);
        return this;
    }

    public void setTypeDocumentId(Long typeDocumentId) {
        this.typeDocumentId = typeDocumentId;
    }

    public String getLabelDestinataire() {
        return this.labelDestinataire;
    }

    public Corresp labelDestinataire(String labelDestinataire) {
        this.setLabelDestinataire(labelDestinataire);
        return this;
    }

    public void setLabelDestinataire(String labelDestinataire) {
        this.labelDestinataire = labelDestinataire;
    }

    public String getLabelContactCC() {
        return this.labelContactCC;
    }

    public Corresp labelContactCC(String labelContactCC) {
        this.setLabelContactCC(labelContactCC);
        return this;
    }

    public void setLabelContactCC(String labelContactCC) {
        this.labelContactCC = labelContactCC;
    }

    public Long getIdSociete() {
        return this.idSociete;
    }

    public Corresp idSociete(Long idSociete) {
        this.setIdSociete(idSociete);
        return this;
    }

    public void setIdSociete(Long idSociete) {
        this.idSociete = idSociete;
    }

    public String getNomSociete() {
        return this.nomSociete;
    }

    public Corresp nomSociete(String nomSociete) {
        this.setNomSociete(nomSociete);
        return this;
    }

    public void setNomSociete(String nomSociete) {
        this.nomSociete = nomSociete;
    }

    public String getCodeUniqueSociete() {
        return this.codeUniqueSociete;
    }

    public Corresp codeUniqueSociete(String codeUniqueSociete) {
        this.setCodeUniqueSociete(codeUniqueSociete);
        return this;
    }

    public void setCodeUniqueSociete(String codeUniqueSociete) {
        this.codeUniqueSociete = codeUniqueSociete;
    }

    public Set<Dossier> getDossiers() {
        return this.dossiers;
    }

    public void setDossiers(Set<Dossier> dossiers) {
        this.dossiers = dossiers;
    }

    public Corresp dossiers(Set<Dossier> dossiers) {
        this.setDossiers(dossiers);
        return this;
    }

    public Corresp addDossiers(Dossier dossier) {
        this.dossiers.add(dossier);
        dossier.getCorresps().add(this);
        return this;
    }

    public Corresp removeDossiers(Dossier dossier) {
        this.dossiers.remove(dossier);
        dossier.getCorresps().remove(this);
        return this;
    }

    public Set<Bordereaux> getBordereaux() {
        return this.bordereaux;
    }

    public void setBordereaux(Set<Bordereaux> bordereaux) {
        this.bordereaux = bordereaux;
    }

    public Corresp bordereaux(Set<Bordereaux> bordereaux) {
        this.setBordereaux(bordereaux);
        return this;
    }

    public Corresp addBordereaux(Bordereaux bordereaux) {
        this.bordereaux.add(bordereaux);
        bordereaux.getCorresps().add(this);
        return this;
    }

    public Corresp removeBordereaux(Bordereaux bordereaux) {
        this.bordereaux.remove(bordereaux);
        bordereaux.getCorresps().remove(this);
        return this;
    }

    public Set<Pj> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(Set<Pj> pjs) {
        this.attachments = pjs;
    }

    public Corresp attachments(Set<Pj> pjs) {
        this.setAttachments(pjs);
        return this;
    }

    public Corresp addAttachments(Pj pj) {
        this.attachments.add(pj);
        pj.getCorresps().add(this);
        return this;
    }

    public Corresp removeAttachments(Pj pj) {
        this.attachments.remove(pj);
        pj.getCorresps().remove(this);
        return this;
    }

    public Set<Tasks> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Tasks> tasks) {
        this.tasks = tasks;
    }

    public Corresp tasks(Set<Tasks> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public Corresp addTasks(Tasks tasks) {
        this.tasks.add(tasks);
        tasks.getCorresps().add(this);
        return this;
    }

    public Corresp removeTasks(Tasks tasks) {
        this.tasks.remove(tasks);
        tasks.getCorresps().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Corresp)) {
            return false;
        }
        return id != null && id.equals(((Corresp) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Corresp{" +
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
            ", idSociete=" + getIdSociete() +
            ", nomSociete='" + getNomSociete() + "'" +
            ", codeUniqueSociete='" + getCodeUniqueSociete() + "'" +
            "}";
    }
}
