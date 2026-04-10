package com.ooad.lms.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_enrolled_courses", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "course_id")
    private final List<Long> enrolledCourses = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_submissions", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "submission_id")
    private final List<Long> submissions = new ArrayList<>();

    protected Student() {
    }

    public Student(Long userId, String name, String email, String password) {
        super(userId, name, email, password, Role.STUDENT);
    }

    public void enrollCourse(Long courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
        }
    }

    public void submitAssignment(Long submissionId) {
        submissions.add(submissionId);
    }

    public List<Long> viewGrades() {
        return List.copyOf(submissions);
    }

    public List<Long> viewProgress() {
        return List.copyOf(enrolledCourses);
    }

    public List<Long> getEnrolledCourses() {
        return enrolledCourses;
    }

    public List<Long> getSubmissions() {
        return submissions;
    }
}