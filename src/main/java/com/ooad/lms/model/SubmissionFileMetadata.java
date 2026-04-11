package com.ooad.lms.model;

public class SubmissionFileMetadata implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long submissionId;
    private Long studentId;
    private String storagePath;
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
