package com.ooad.lms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ooad.lms.dto.CreateAssignmentRequest;
import com.ooad.lms.dto.CreateExamRequest;
import com.ooad.lms.dto.CreateModuleRequest;
import com.ooad.lms.dto.GradeSubmissionRequest;
import com.ooad.lms.dto.UploadMaterialRequest;
import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Exam;
import com.ooad.lms.model.Material;
import com.ooad.lms.model.Module;
import com.ooad.lms.model.Submission;
import com.ooad.lms.service.CourseService;
import com.ooad.lms.service.InstructorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {
    private final CourseService courseService;
    private final InstructorService instructorService;

    public InstructorController(CourseService courseService, InstructorService instructorService) {
        this.courseService = courseService;
        this.instructorService = instructorService;
    }

    @PostMapping("/courses/{courseId}/modules")
    public Module createModule(@PathVariable Long courseId, @RequestParam Long instructorId, @Valid @RequestBody CreateModuleRequest request) {
        return courseService.addModule(instructorId, courseId, request);
    }

    @PostMapping("/courses/{courseId}/modules/{moduleId}/materials")
    public Material uploadMaterial(
            @PathVariable Long courseId,
            @PathVariable Long moduleId,
            @RequestParam Long instructorId,
            @Valid @RequestBody UploadMaterialRequest request
    ) {
        return courseService.addMaterial(instructorId, courseId, moduleId, request);
    }

    @PostMapping("/courses/{courseId}/assignments")
    public Assignment createAssignment(
            @PathVariable Long courseId,
            @RequestParam Long instructorId,
            @Valid @RequestBody CreateAssignmentRequest request
    ) {
        return courseService.createAssignment(instructorId, courseId, request);
    }

    @PostMapping("/courses/{courseId}/exams")
    public Exam createExam(
            @PathVariable Long courseId,
            @RequestParam Long instructorId,
            @Valid @RequestBody CreateExamRequest request
    ) {
        return courseService.addExam(instructorId, courseId, request);
    }

    @GetMapping("/courses/{courseId}/assignments/{assignmentId}/submissions")
    public List<Submission> getSubmissions(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam Long instructorId
    ) {
        return instructorService.getSubmissions(instructorId, courseId, assignmentId);
    }

    @PostMapping("/courses/{courseId}/submissions/{submissionId}/grade")
    public Submission gradeSubmission(
            @PathVariable Long courseId,
            @PathVariable Long submissionId,
            @RequestParam Long instructorId,
            @Valid @RequestBody GradeSubmissionRequest request
    ) {
        return instructorService.gradeSubmission(instructorId, courseId, submissionId, request);
    }

    @GetMapping("/courses/{courseId}/students")
    public List<Long> viewStudentProgress(@PathVariable Long courseId, @RequestParam Long instructorId) {
        return instructorService.viewStudentProgress(instructorId, courseId);
    }
}