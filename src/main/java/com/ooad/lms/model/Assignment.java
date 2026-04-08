package com.ooad.lms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Assignment {
    private Long assignmentId;
    private String description;
    private LocalDateTime deadline;
    private boolean published;
    private final List<Long> submissions = new ArrayList<>();

    public Assignment(Long assignmentId, String description, LocalDateTime deadline) {
        this.assignmentId = assignmentId;
        this.description = description;
        this.deadline = deadline;
    }

    public void publish() {
        this.published = true;
    }

    public void evaluate(Long submissionId) {
        if (!submissions.contains(submissionId)) {
            submissions.add(submissionId);
        }
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public boolean isPublished() {
        return published;
    }

    public List<Long> getSubmissions() {
        return submissions;
    }
}