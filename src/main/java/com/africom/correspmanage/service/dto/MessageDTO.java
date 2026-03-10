package com.africom.correspmanage.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A DTO for the {@link com.africom.correspmanage.domain.Message} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageDTO implements Serializable {

    private Long id;

    private String sender;

    private List<Map<String, String>> receiver;

    private List<Map<String, String>> copyTo;

    private List<Map<String, String>> blindCopyTo;

    private String path;

    private String msgID;

    private String parentID;

    private String inReplyTo;

    private String type;

    private String etat;

    private Long correspId;

    private Long fileId;

    private String uniqueName;

    private String createdBy;

    private ZonedDateTime creationDate;

    private String updatedBy;

    private ZonedDateTime updatedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<Map<String, String>> getReceiver() {
        return receiver;
    }

    public void setReceiver(List<Map<String, String>> receiver) {
        this.receiver = receiver;
    }

    public List<Map<String, String>> getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(List<Map<String, String>> copyTo) {
        this.copyTo = copyTo;
    }

    public List<Map<String, String>> getBlindCopyTo() {
        return blindCopyTo;
    }

    public void setBlindCopyTo(List<Map<String, String>> blindCopyTo) {
        this.blindCopyTo = blindCopyTo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Long getCorrespId() {
        return correspId;
    }

    public void setCorrespId(Long correspId) {
        this.correspId = correspId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageDTO)) {
            return false;
        }

        MessageDTO messageDTO = (MessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, messageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageDTO{" +
            "id=" + getId() +
            ", sender='" + getSender() + "'" +
            ", receiver='" + String.valueOf(getReceiver()) + "'" +
            ", copyTo='" + String.valueOf(getCopyTo()) + "'" +
            ", blindCopyTo='" + String.valueOf(getBlindCopyTo()) + "'" +
            ", path='" + getPath() + "'" +
            ", msgID='" + getMsgID() + "'" +
            ", parentID='" + getParentID() + "'" +
            ", inReplyTo='" + getInReplyTo() + "'" +
            ", type='" + getType() + "'" +
            ", etat='" + getEtat() + "'" +
            ", correspId=" + getCorrespId() +
            ", fileId=" + getFileId() +
            ", uniqueName='" + getUniqueName() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", updatedBy='" + getUpdatedBy() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            "}";
    }
}
