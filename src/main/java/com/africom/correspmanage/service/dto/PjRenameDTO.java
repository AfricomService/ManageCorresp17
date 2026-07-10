package com.africom.correspmanage.service.dto;

import javax.validation.constraints.NotBlank;

/**
 * DTO minimaliste pour le renommage d'une PJ.
 */
public class PjRenameDTO {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
