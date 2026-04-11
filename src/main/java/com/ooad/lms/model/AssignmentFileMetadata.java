package com.ooad.lms.model;

public class AssignmentFileMetadata implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long assignmentId;
    private String storagePath;
    private String originalFileName;

    protected AssignmentFileMetadata() {
    }

    public AssignmentFileMetadata(Long assignmentId, String storagePath, String originalFileName) {
        this.assignmentId = assignmentId;
        this.storagePath = storagePath;
        this.originalFileName = originalFileName;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }
}
