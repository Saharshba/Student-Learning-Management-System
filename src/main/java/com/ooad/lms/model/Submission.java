package com.ooad.lms.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    private Long studentId;

    private Long assignmentId;

    private LocalDateTime timestamp;

    private Double grade;

    private String content;

    private String feedback;

    protected Submission() {
    }

    public Submission(Long submissionId, Long studentId, Long assignmentId, LocalDateTime timestamp, String content) {
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.timestamp = timestamp;
        this.content = content;
    }

    public void submit() {
        this.timestamp = LocalDateTime.now();
    }

    public void updateGrade(Double grade, String feedback) {
        this.grade = grade;
        this.feedback = feedback;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Double getGrade() {
        return grade;
    }

    public String getContent() {
        return content;
    }

    public String getFeedback() {
        return feedback;
    }
}