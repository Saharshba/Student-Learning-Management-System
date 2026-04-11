package com.ooad.lms.model;

import java.time.LocalDateTime;

public class Material implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long fileId;
    private MaterialType fileType;
    private LocalDateTime uploadDate;
    private String name;
    private String contentUrl;

    public Material(Long fileId, MaterialType fileType, LocalDateTime uploadDate, String name, String contentUrl) {
        this.fileId = fileId;
        this.fileType = fileType;
        this.uploadDate = uploadDate;
        this.name = name;
        this.contentUrl = contentUrl;
    }

    public String getContent() {
        return contentUrl;
    }

    public Long getFileId() {
        return fileId;
    }

    public MaterialType getFileType() {
        return fileType;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public String getName() {
        return name;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}