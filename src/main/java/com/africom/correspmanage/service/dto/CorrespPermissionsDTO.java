package com.africom.correspmanage.service.dto;

import java.util.HashMap;
import java.util.Map;

public class CorrespPermissionsDTO {
    private PermissionDTO corresp = new PermissionDTO();
    private Map<Long, PermissionDTO> tasksPermissions = new HashMap<>();

    public PermissionDTO getCorresp() { return corresp; }
    public void setCorresp(PermissionDTO corresp) { this.corresp = corresp; }

    public Map<Long, PermissionDTO> getTasksPermissions() { return tasksPermissions; }
    public void setTasksPermissions(Map<Long, PermissionDTO> tasksPermissions) { this.tasksPermissions = tasksPermissions; }
}
