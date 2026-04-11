package com.africom.correspmanage.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
    @TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
})
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "sender")
    private String sender;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "receiver")
    private List<Map<String, String>> receiver;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "copy_to")
    private List<Map<String, String>> copyTo;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "blind_copy_to")
    private List<Map<String, String>> blindCopyTo;

    @Column(name = "path")
    private String path;

    @Column(name = "msg_id")
    private String msgID;

    @Column(name = "parent_id")
    private String parentID;

    @Column(name = "in_reply_to")
    private String inReplyTo;

    @Column(name = "type")
    private String type;

    @Column(name = "etat")
    private String etat;

    @Column(name = "corresp_id")
    private Long correspId;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "unique_name")
    private String uniqueName;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "message_type")
    private String messageType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return this.sender;
    }

    public Message sender(String sender) {
        this.setSender(sender);
        return this;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public List<Map<String, String>> getReceiver() {
        return this.receiver;
    }

    public Message receiver(List<Map<String, String>> receiver) {
        this.setReceiver(receiver);
        return this;
    }

    public void setReceiver(List<Map<String, String>> receiver) {
        this.receiver = receiver;
    }

    public List<Map<String, String>> getCopyTo() {
        return this.copyTo;
    }

    public Message copyTo(List<Map<String, String>> copyTo) {
        this.setCopyTo(copyTo);
        return this;
    }

    public void setCopyTo(List<Map<String, String>> copyTo) {
        this.copyTo = copyTo;
    }

    public List<Map<String, String>> getBlindCopyTo() {
        return this.blindCopyTo;
    }

    public Message blindCopyTo(List<Map<String, String>> blindCopyTo) {
        this.setBlindCopyTo(blindCopyTo);
        return this;
    }

    public void setBlindCopyTo(List<Map<String, String>> blindCopyTo) {
        this.blindCopyTo = blindCopyTo;
    }

    public String getPath() {
        return this.path;
    }

    public Message path(String path) {
        this.setPath(path);
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMsgID() {
        return this.msgID;
    }

    public Message msgID(String msgID) {
        this.setMsgID(msgID);
        return this;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getParentID() {
        return this.parentID;
    }

    public Message parentID(String parentID) {
        this.setParentID(parentID);
        return this;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getInReplyTo() {
        return this.inReplyTo;
    }

    public Message inReplyTo(String inReplyTo) {
        this.setInReplyTo(inReplyTo);
        return this;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getType() {
        return this.type;
    }

    public Message type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtat() {
        return this.etat;
    }

    public Message etat(String etat) {
        this.setEtat(etat);
        return this;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Long getCorrespId() {
        return this.correspId;
    }

    public Message correspId(Long correspId) {
        this.setCorrespId(correspId);
        return this;
    }

    public void setCorrespId(Long correspId) {
        this.correspId = correspId;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public Message fileId(Long fileId) {
        this.setFileId(fileId);
        return this;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getUniqueName() {
        return this.uniqueName;
    }

    public Message uniqueName(String uniqueName) {
        this.setUniqueName(uniqueName);
        return this;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Message createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public Message creationDate(ZonedDateTime creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public Message updatedBy(String updatedBy) {
        this.setUpdatedBy(updatedBy);
        return this;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Message updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public Message messageType(String messageType) {
        this.setMessageType(messageType);
        return this;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return id != null && id.equals(((Message) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", sender='" + getSender() + "'" +
            ", receiver='" + getReceiver() + "'" +
            ", copyTo='" + getCopyTo() + "'" +
            ", blindCopyTo='" + getBlindCopyTo() + "'" +
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
            ", messageType='" + getMessageType() + "'" +
            "}";
    }
}
