package com.ooad.lms.controller;

import com.ooad.lms.dto.SubmitAssignmentRequest;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Submission;
import com.ooad.lms.service.CourseService;
import com.ooad.lms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;
    private final CourseService courseService;

    public StudentController(StudentService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public List<Course> getAvailableCourses() {
        return courseService.getAllCourses();
    }

    @PostMapping("/{studentId}/courses/{courseId}/enroll")
    public Course enroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        return studentService.enroll(studentId, courseId);
    }

    @PostMapping("/{studentId}/assignments/{assignmentId}/submit")
    public Submission submitAssignment(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody SubmitAssignmentRequest request
    ) {
        return studentService.submitAssignment(studentId, assignmentId, request);
    }

    @GetMapping("/{studentId}/grades")
    public List<Submission> getGrades(@PathVariable Long studentId) {
        return studentService.getGrades(studentId);
    }

    @GetMapping("/{studentId}/progress")
    public Map<Long, Double> getProgress(@PathVariable Long studentId) {
        return studentService.getProgress(studentId);
    }

    @GetMapping("/{studentId}/notifications")
    public List<String> getNotifications(@PathVariable Long studentId) {
        return studentService.getDeadlineNotifications(studentId);
    }

    @GetMapping("/{studentId}/materials/{materialId}/download")
    public ResponseEntity<Resource> downloadMaterial(
            @PathVariable Long studentId,
            @PathVariable Long materialId
    ) {
        StudentService.MaterialDownload download = studentService.downloadMaterial(studentId, materialId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(download.fileName()).build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(download.resource());
    }
}