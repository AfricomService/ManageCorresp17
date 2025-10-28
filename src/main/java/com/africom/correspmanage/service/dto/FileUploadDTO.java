package com.africom.correspmanage.service.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadDTO {

    private String fileId;
    private String uniqueFileName;

    public FileUploadDTO() {}

    public FileUploadDTO(String fileId, String uniqueFileName) {
        this.fileId = fileId;
        this.uniqueFileName = uniqueFileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUniqueFileName() {
        return uniqueFileName;
    }

    public void setUniqueFileName(String uniqueFileName) {
        this.uniqueFileName = uniqueFileName;
    }
}
