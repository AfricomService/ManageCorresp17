package com.africom.correspmanage.service.dto;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.Tasks} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TasksDTO implements Serializable {

    private Long id;

    @NotNull
    private String object;

    @Lob
    private byte[] content;

    private String contentContentType;
    private ZonedDateTime creationDate;

    private ZonedDateTime startDate;

    private ZonedDateTime dueDate;

    private ZonedDateTime closeDate;

    private String responsible;

    private CorrespStatus status;

    private String createdBy;

    private String updatedBy;

    private ZonedDateTime updatedDate;

    private String taskType;

    private String responsableName;

    private String commentaire;

    private ZonedDateTime dateTraitement;

    private Long userTraitement;

    private String commentaireTraitement;

    private String serviceCreateurTask;

    private CorrespDTO parentDoc;

    private TasksDTO parentTask;

    /**
     * Nouvelle propriété : liste des responsables liés à la tâche.
     * Chaque ResponsableTacheDTO contient soit un codeDept soit un matriculePers (ou les deux).
     */
    private List<ResponsableTacheDTO> responsables = new ArrayList<>();

    public String getResponsableName() {
        return responsableName;
    }

    public void setResponsableName(String responsableName) {
        this.responsableName = responsableName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentContentType() {
        return contentContentType;
    }

    public void setContentContentType(String contentContentType) {
        this.contentContentType = contentContentType;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public ZonedDateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(ZonedDateTime closeDate) {
        this.closeDate = closeDate;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public CorrespStatus getStatus() {
        return status;
    }

    public void setStatus(CorrespStatus status) {
        this.status = status;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public ZonedDateTime getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(ZonedDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public Long getUserTraitement() {
        return userTraitement;
    }

    public void setUserTraitement(Long userTraitement) {
        this.userTraitement = userTraitement;
    }

    public String getCommentaireTraitement() {
        return commentaireTraitement;
    }

    public void setCommentaireTraitement(String commentaireTraitement) {
        this.commentaireTraitement = commentaireTraitement;
    }

    public String getServiceCreateurTask() {
        return serviceCreateurTask;
    }

    public void setServiceCreateurTask(String serviceCreateurTask) {
        this.serviceCreateurTask = serviceCreateurTask;
    }

    public CorrespDTO getParentDoc() {
        return parentDoc;
    }

    public void setParentDoc(CorrespDTO parentDoc) {
        this.parentDoc = parentDoc;
    }

    public TasksDTO getParentTask() {
        return parentTask;
    }

    public void setParentTask(TasksDTO parentTask) {
        this.parentTask = parentTask;
    }

    public List<ResponsableTacheDTO> getResponsables() {
        return responsables;
    }

    public void setResponsables(List<ResponsableTacheDTO> responsables) {
        this.responsables = responsables != null ? responsables : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TasksDTO)) {
            return false;
        }

        TasksDTO tasksDTO = (TasksDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tasksDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TasksDTO{" +
            "id=" + getId() +
            ", object='" + getObject() + "'" +
            ", content='" + getContent() + "'" +
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
            ", parentDoc=" + getParentDoc() +
            ", parentTask=" + getParentTask() +
            ", responsablesCount=" + (responsables != null ? responsables.size() : 0) +
            "}";
    }
}
