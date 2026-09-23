package com.africom.correspmanage.domain;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Tasks.
 */
@Entity
@Table(name = "tasks")
public class Tasks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "object", nullable = false)
    private String object;

    @Lob
    @Column(name = "content")
    private byte[] content;

    @Column(name = "content_content_type")
    private String contentContentType;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "start_date")
    private ZonedDateTime startDate;

    @Column(name = "due_date")
    private ZonedDateTime dueDate;

    @Column(name = "close_date")
    private ZonedDateTime closeDate;

    @Column(name = "responsible")
    private String responsible;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CorrespStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "date_traitement")
    private ZonedDateTime dateTraitement;

    @Column(name = "user_traitement")
    private Long userTraitement;

    @Column(name = "commentaire_traitement")
    private String commentaireTraitement;

    @Column(name = "service_createur_task")
    private String serviceCreateurTask;

    @Column(name = "user_keycloak_trait")
    private String userKeycloakTrait;

    @Column(name = "date_prise_en_charge")
    private ZonedDateTime datePriseEnCharge;

    @ManyToOne
    @JsonIgnoreProperties(value = { "dossiers", "bordereaux", "attachments", "tasks" }, allowSetters = true)
    private Corresp parentDoc;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parentDoc", "parentTask", "corresps" }, allowSetters = true)
    private Tasks parentTask;

    @ManyToMany(mappedBy = "tasks")
    @JsonIgnoreProperties(value = { "dossiers", "bordereaux", "attachments", "tasks" }, allowSetters = true)
    private Set<Corresp> corresps = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tasks id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObject() {
        return this.object;
    }

    public Tasks object(String object) {
        this.setObject(object);
        return this;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public byte[] getContent() {
        return this.content;
    }

    public Tasks content(byte[] content) {
        this.setContent(content);
        return this;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return this.contentContentType;
    }

    public Tasks contentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
        return this;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public Tasks creationDate(ZonedDateTime creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getStartDate() {
        return this.startDate;
    }

    public Tasks startDate(ZonedDateTime startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getDueDate() {
        return this.dueDate;
    }

    public Tasks dueDate(ZonedDateTime dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public ZonedDateTime getCloseDate() {
        return this.closeDate;
    }

    public Tasks closeDate(ZonedDateTime closeDate) {
        this.setCloseDate(closeDate);
        return this;
    }

    public void setCloseDate(ZonedDateTime closeDate) {
        this.closeDate = closeDate;
    }

    public String getResponsible() {
        return this.responsible;
    }

    public Tasks responsible(String responsible) {
        this.setResponsible(responsible);
        return this;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public CorrespStatus getStatus() {
        return this.status;
    }

    public Tasks status(CorrespStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CorrespStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Tasks createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Tasks updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Tasks updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getTaskType() {
        return this.taskType;
    }

    public Tasks taskType(String taskType) {
        this.setTaskType(taskType);
        return this;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public Tasks commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public ZonedDateTime getDateTraitement() {
        return this.dateTraitement;
    }

    public Tasks dateTraitement(ZonedDateTime dateTraitement) {
        this.setDateTraitement(dateTraitement);
        return this;
    }

    public void setDateTraitement(ZonedDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public Long getUserTraitement() {
        return this.userTraitement;
    }

    public Tasks userTraitement(Long userTraitement) {
        this.setUserTraitement(userTraitement);
        return this;
    }

    public void setUserTraitement(Long userTraitement) {
        this.userTraitement = userTraitement;
    }

    public String getCommentaireTraitement() {
        return this.commentaireTraitement;
    }

    public Tasks commentaireTraitement(String commentaireTraitement) {
        this.setCommentaireTraitement(commentaireTraitement);
        return this;
    }

    public void setCommentaireTraitement(String commentaireTraitement) {
        this.commentaireTraitement = commentaireTraitement;
    }

    public String getServiceCreateurTask() {
        return this.serviceCreateurTask;
    }

    public Tasks serviceCreateurTask(String serviceCreateurTask) {
        this.setServiceCreateurTask(serviceCreateurTask);
        return this;
    }

    public void setServiceCreateurTask(String serviceCreateurTask) {
        this.serviceCreateurTask = serviceCreateurTask;
    }

    public String getUserKeycloakTrait() {
        return this.userKeycloakTrait;
    }

    public Tasks userKeycloakTrait(String userKeycloakTrait) {
        this.setUserKeycloakTrait(userKeycloakTrait);
        return this;
    }

    public void setUserKeycloakTrait(String userKeycloakTrait) {
        this.userKeycloakTrait = userKeycloakTrait;
    }

    public ZonedDateTime getDatePriseEnCharge() {
        return this.datePriseEnCharge;
    }

    public Tasks datePriseEnCharge(ZonedDateTime datePriseEnCharge) {
        this.setDatePriseEnCharge(datePriseEnCharge);
        return this;
    }

    public void setDatePriseEnCharge(ZonedDateTime datePriseEnCharge) {
        this.datePriseEnCharge = datePriseEnCharge;
    }

    public Corresp getParentDoc() {
        return this.parentDoc;
    }

    public void setParentDoc(Corresp corresp) {
        this.parentDoc = corresp;
    }

    public Tasks parentDoc(Corresp corresp) {
        this.setParentDoc(corresp);
        return this;
    }

    public Tasks getParentTask() {
        return this.parentTask;
    }

    public void setParentTask(Tasks tasks) {
        this.parentTask = tasks;
    }

    public Tasks parentTask(Tasks tasks) {
        this.setParentTask(tasks);
        return this;
    }

    public Set<Corresp> getCorresps() {
        return this.corresps;
    }

    public void setCorresps(Set<Corresp> corresps) {
        if (this.corresps != null) {
            this.corresps.forEach(i -> i.removeTasks(this));
        }
        if (corresps != null) {
            corresps.forEach(i -> i.addTasks(this));
        }
        this.corresps = corresps;
    }

    public Tasks corresps(Set<Corresp> corresps) {
        this.setCorresps(corresps);
        return this;
    }

    public Tasks addCorresps(Corresp corresp) {
        this.corresps.add(corresp);
        corresp.getTasks().add(this);
        return this;
    }

    public Tasks removeCorresps(Corresp corresp) {
        this.corresps.remove(corresp);
        corresp.getTasks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tasks)) {
            return false;
        }
        return id != null && id.equals(((Tasks) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tasks{" +
            "id=" + getId() +
            ", object='" + getObject() + "'" +
            ", content='" + getContent() + "'" +
            ", contentContentType='" + getContentContentType() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", closeDate='" + getCloseDate() + "'" +
            ", responsible='" + getResponsible() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", taskType='" + getTaskType() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            ", dateTraitement='" + getDateTraitement() + "'" +
            ", userTraitement=" + getUserTraitement() +
            ", commentaireTraitement='" + getCommentaireTraitement() + "'" +
            ", serviceCreateurTask='" + getServiceCreateurTask() + "'" +
            ", userKeycloakTrait='" + getUserKeycloakTrait() + "'" +
            ", datePriseEnCharge='" + getDatePriseEnCharge() + "'" +
            "}";
    }
}
