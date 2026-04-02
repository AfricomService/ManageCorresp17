package com.africom.correspmanage.service.dto;

public class PermissionDTO {
    private boolean canRead;
    private boolean canWrite;
    private boolean canDelete;
    private boolean canAdmin;

    public PermissionDTO() {}

    // getters / setters
    public boolean isCanRead() { return canRead; }
    public void setCanRead(boolean canRead) { this.canRead = canRead; }
    public boolean isCanWrite() { return canWrite; }
    public void setCanWrite(boolean canWrite) { this.canWrite = canWrite; }
    public boolean isCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }
    public boolean isCanAdmin() { return canAdmin; }
    public void setCanAdmin(boolean canAdmin) { this.canAdmin = canAdmin; }
}
