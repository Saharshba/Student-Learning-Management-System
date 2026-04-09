package com.ooad.lms.model;

import java.time.LocalDateTime;

public class Material {
    private final Long fileId;
    private final MaterialType fileType;
    private final LocalDateTime uploadDate;
    private final String name;
    private final String contentUrl;

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
}