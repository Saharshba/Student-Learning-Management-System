package com.ooad.lms.model;

public class MaterialFileMetadata implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long fileId;
    private String storagePath;
    private String originalFileName;

    public MaterialFileMetadata() {}

    public MaterialFileMetadata(Long fileId, String storagePath, String originalFileName) {
        this.fileId = fileId;
        this.storagePath = storagePath;
        this.originalFileName = originalFileName;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}