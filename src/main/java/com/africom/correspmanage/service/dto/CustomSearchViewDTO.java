package com.africom.correspmanage.service.dto;

import java.util.Map;

public class CustomSearchViewDTO {

    private String name;
    private Map<String, String> filters;

    public CustomSearchViewDTO(String name, Map<String, String> filters) {
        this.name = name;
        this.filters = filters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }
}
