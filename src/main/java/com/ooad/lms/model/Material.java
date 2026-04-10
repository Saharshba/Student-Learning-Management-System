package com.ooad.lms.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Enumerated(EnumType.STRING)
    private MaterialType fileType;

    private LocalDateTime uploadDate;

    private String name;

    private String contentUrl;

    protected Material() {
    }

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