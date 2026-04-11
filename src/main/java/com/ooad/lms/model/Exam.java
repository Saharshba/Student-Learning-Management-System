package com.ooad.lms.model;

import java.time.LocalDateTime;

public class Exam implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String description;
    private LocalDateTime scheduledDate;
    private String fileType;
    private String fileUrl;
    private LocalDateTime createdAt;

    public Exam(Long id, String title, String description, LocalDateTime scheduledDate,
                String fileType, String fileUrl, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.scheduledDate = scheduledDate;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}