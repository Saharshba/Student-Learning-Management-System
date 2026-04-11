package com.ooad.lms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "assignment_file_metadata")
public class AssignmentFileMetadata {
    @Id
    private Long assignmentId;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
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
