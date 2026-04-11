package com.ooad.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Long> enrolledCourses = new ArrayList<>();
    private final List<Long> submissions = new ArrayList<>();

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