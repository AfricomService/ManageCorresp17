package com.africom.correspmanage.service.dto;

import com.africom.correspmanage.domain.enumeration.CorrespStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DTO léger utilisé exclusivement par getAllTasksByUserAclPaged.
 * Ne contient que les champs nécessaires à l'affichage de la liste paginée côté home.
 */
public class PagedTaskDTO implements Serializable {

    private Long id;

    /** Objet / titre de la tâche */
    private String object;

    private ZonedDateTime creationDate;

    private ZonedDateTime dueDate;

    private ZonedDateTime closeDate;

    private CorrespStatus status;

    private String taskType;

    private String commentaire;

    private String commentaireTraitement;

    private ZonedDateTime datePriseEnCharge;

    /** Nom affiché du/des responsable(s) — chaîne concaténée pour le fallback frontend */
    private String responsableName;

    /** Liste détaillée des responsables (dept ou personne) */
    private List<ResponsableTacheDTO> responsables = new ArrayList<>();

//    private String correspSubject;
//
//    private String correspReference;

//    private String identifiantCorresp;

    // ── Getters / Setters ──────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }

    public ZonedDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(ZonedDateTime creationDate) { this.creationDate = creationDate; }

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

    public CorrespStatus getStatus() {
        return status;
    }
    public void setStatus(CorrespStatus status) { this.status = status; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public String getResponsableName() { return responsableName; }
    public void setResponsableName(String responsableName) { this.responsableName = responsableName; }

    public List<ResponsableTacheDTO> getResponsables() { return responsables; }
    public void setResponsables(List<ResponsableTacheDTO> responsables) {
        this.responsables = responsables != null ? responsables : new ArrayList<>();
    }

    public String getCommentaireTraitement() {
        return commentaireTraitement;
    }

    public void setCommentaireTraitement(String commentaireTraitement) {
        this.commentaireTraitement = commentaireTraitement;
    }

    public ZonedDateTime getDatePriseEnCharge() {
        return datePriseEnCharge;
    }

    public void setDatePriseEnCharge(ZonedDateTime datePriseEnCharge) {
        this.datePriseEnCharge = datePriseEnCharge;
    }

//    public String getCorrespSubject() { return correspSubject; }
//    public void setCorrespSubject(String correspSubject) { this.correspSubject = correspSubject; }
//
//    public String getCorrespReference() { return correspReference; }
//    public void setCorrespReference(String correspReference) { this.correspReference = correspReference; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PagedTaskDTO)) return false;
        PagedTaskDTO that = (PagedTaskDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "PagedTaskDTO{id=" + id + ", status=" + status + ", object='" + object + "'}";
    }
}
