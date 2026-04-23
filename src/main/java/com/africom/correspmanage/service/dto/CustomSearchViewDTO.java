package com.africom.correspmanage.service.dto;

import java.util.Map;

public class CustomSearchViewDTO {
    private Long id;
    private String name;
    private Map<String, String> filters;

    // ← Remplacer l'ancien constructeur par celui-ci
    public CustomSearchViewDTO(Long id, String name, Map<String, String> filters) {
        this.id = id;
        this.name = name;
        this.filters = filters;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, String> getFilters() { return filters; }
    public void setFilters(Map<String, String> filters) { this.filters = filters; }
}
