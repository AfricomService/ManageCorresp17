package com.africom.correspmanage.client;

import java.util.UUID;

public class ModelNotifDTO {

    private UUID id;
    private String name;
    private String famille;
    private String code;
    private String content;           // le template FreeMarker
    private String subjectFormula;    // peut aussi être un template FreeMarker
    private String senderEmailFormule;
    private String recepientEmailFormule;
    private Boolean saveCopy;

    // Getters / Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFamille() { return famille; }
    public void setFamille(String famille) { this.famille = famille; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSubjectFormula() { return subjectFormula; }
    public void setSubjectFormula(String subjectFormula) { this.subjectFormula = subjectFormula; }

    public String getSenderEmailFormule() { return senderEmailFormule; }
    public void setSenderEmailFormule(String senderEmailFormule) { this.senderEmailFormule = senderEmailFormule; }

    public String getRecepientEmailFormule() { return recepientEmailFormule; }
    public void setRecepientEmailFormule(String recepientEmailFormule) { this.recepientEmailFormule = recepientEmailFormule; }

    public Boolean getSaveCopy() {
        return saveCopy;
    }

    public void setSaveCopy(Boolean saveCopy) {
        this.saveCopy = saveCopy;
    }
}
