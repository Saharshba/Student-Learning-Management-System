package com.ooad.lms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "submission_file_metadata")
public class SubmissionFileMetadata {
    @Id
    private Long submissionId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private String originalFileName;

    protected SubmissionFileMetadata() {
    }

    public SubmissionFileMetadata(Long submissionId, Long studentId, String storagePath, String originalFileName) {
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.storagePath = storagePath;
        this.originalFileName = originalFileName;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }
}
