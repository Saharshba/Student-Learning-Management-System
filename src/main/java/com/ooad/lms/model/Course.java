package com.ooad.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private final Long courseId;
    private final String title;
    private final String description;
    private Long instructorId;
    private final List<Module> modules = new ArrayList<>();
    private final List<Assignment> assignments = new ArrayList<>();
    private final List<Exam> exams = new ArrayList<>();
    private final List<Long> enrolledStudentIds = new ArrayList<>();

    public Course(Long courseId, String title, String description) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
    }

    public void addModule(Module module) {
        modules.add(module);
    }

    public void removeModule(Long moduleId) {
        modules.removeIf(module -> module.getModuleId().equals(moduleId));
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void addExam(Exam exam) {
        exams.add(exam);
    }

    public void removeExam(Long examId) {
        exams.removeIf(exam -> exam.getExamId().equals(examId));
    }

    public void enrollStudent(Long studentId) {
        if (!enrolledStudentIds.contains(studentId)) {
            enrolledStudentIds.add(studentId);
        }
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public List<Long> getEnrolledStudentIds() {
        return enrolledStudentIds;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }
}