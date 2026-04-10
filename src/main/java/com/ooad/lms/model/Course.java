package com.ooad.lms.model;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String title;

    private String description;

    private Long instructorId;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private final List<Module> modules = new ArrayList<>();

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private final List<Assignment> assignments = new ArrayList<>();

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private final List<Exam> exams = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_enrolled_students", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "student_id")
    private final List<Long> enrolledStudentIds = new ArrayList<>();

    protected Course() {
    }

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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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