package com.ooad.lms.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    private String description;

    private LocalDateTime deadline;

    private boolean published;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "assignment_submissions", joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "submission_id")
    private final List<Long> submissions = new ArrayList<>();

    protected Assignment() {
    }

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