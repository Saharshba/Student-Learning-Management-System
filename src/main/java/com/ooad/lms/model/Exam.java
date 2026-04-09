package com.ooad.lms.model;

import java.time.LocalDateTime;

public class Exam {
    private final Long examId;
    private final String title;
    private final String description;
    private final LocalDateTime scheduleDateTime;
    private final MaterialType fileType;
    private final String fileUrl;
    private final LocalDateTime uploadDate;

    public Exam(Long examId, String title, String description, LocalDateTime scheduleDateTime, MaterialType fileType, String fileUrl, LocalDateTime uploadDate) {
        this.examId = examId;
        this.title = title;
        this.description = description;
        this.scheduleDateTime = scheduleDateTime;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.uploadDate = uploadDate;
    }

    public Long getExamId() {
        return examId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getScheduleDateTime() {
        return scheduleDateTime;
    }

    public MaterialType getFileType() {
        return fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
}
