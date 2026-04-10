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
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examId;

    private String title;

    private String description;

    private LocalDateTime scheduleDateTime;

    @Enumerated(EnumType.STRING)
    private MaterialType fileType;

    private String fileUrl;

    private LocalDateTime uploadDate;

    protected Exam() {
    }

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
