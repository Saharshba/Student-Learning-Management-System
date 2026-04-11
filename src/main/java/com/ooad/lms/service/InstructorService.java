package com.ooad.lms.service;

import com.ooad.lms.dto.GradeSubmissionRequest;
import com.ooad.lms.dto.SubmissionViewResponse;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Submission;
import com.ooad.lms.model.SubmissionFileMetadata;
import com.ooad.lms.repository.SubmissionFileMetadataRepository;
import com.ooad.lms.repository.SubmissionRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorService {
    private final UserService userService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final SubmissionRepository submissionRepository;
    private final SubmissionFileMetadataRepository submissionFileMetadataRepository;
    private final FileStorageService fileStorageService;

    public InstructorService(
            UserService userService,
            CourseService courseService,
            StudentService studentService,
            SubmissionRepository submissionRepository,
            SubmissionFileMetadataRepository submissionFileMetadataRepository,
            FileStorageService fileStorageService
    ) {
        this.userService = userService;
        this.courseService = courseService;
        this.studentService = studentService;
        this.submissionRepository = submissionRepository;
        this.submissionFileMetadataRepository = submissionFileMetadataRepository;
        this.fileStorageService = fileStorageService;
    }

        public List<SubmissionViewResponse> getSubmissions(Long instructorId, Long courseId, Long assignmentId) {
        validateInstructorCourseAccess(instructorId, courseId);
        Assignment assignment = courseService.getAssignment(assignmentId);
        return assignment.getSubmissions().stream()
                .map(studentService::getSubmission)
            .map(submission -> new SubmissionViewResponse(
                submission.getSubmissionId(),
                submission.getStudentId(),
                submission.getAssignmentId(),
                submission.getTimestamp(),
                submission.getGrade(),
                submission.getContent(),
                submission.getFeedback(),
                submissionFileMetadataRepository.existsBySubmissionId(submission.getSubmissionId())
            ))
                .toList();
    }

    public Submission gradeSubmission(Long instructorId, Long courseId, Long submissionId, GradeSubmissionRequest request) {
        validateInstructorCourseAccess(instructorId, courseId);
        Submission submission = studentService.getSubmission(submissionId);
        boolean belongsToCourse = courseService.getCourse(courseId).getAssignments().stream()
                .anyMatch(assignment -> assignment.getSubmissions().contains(submissionId));
        if (!belongsToCourse) {
            throw new BadRequestException("Submission does not belong to the selected course");
        }
        submission.updateGrade(request.grade(), request.feedback());
        return submissionRepository.saveAndFlush(submission);
    }

    public StudentService.MaterialDownload downloadSubmittedAssignmentPdf(Long instructorId, Long courseId, Long submissionId) {
        validateInstructorCourseAccess(instructorId, courseId);

        Submission submission = studentService.getSubmission(submissionId);
        boolean belongsToCourse = courseService.getCourse(courseId).getAssignments().stream()
                .anyMatch(assignment -> assignment.getSubmissions().contains(submissionId));
        if (!belongsToCourse) {
            throw new BadRequestException("Submission does not belong to the selected course");
        }

        SubmissionFileMetadata metadata = submissionFileMetadataRepository.findBySubmissionId(submission.getSubmissionId())
                .orElseThrow(() -> new NotFoundException("No PDF file attached to this submission"));

        Resource resource = fileStorageService.loadAsResource(metadata.getStoragePath());
        return new StudentService.MaterialDownload(resource, metadata.getOriginalFileName());
    }

    public List<Long> viewStudentProgress(Long instructorId, Long courseId) {
        validateInstructorCourseAccess(instructorId, courseId);
        return courseService.getCourse(courseId).getEnrolledStudentIds();
    }

    private void validateInstructorCourseAccess(Long instructorId, Long courseId) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);
        Course course = courseService.getCourse(courseId);
        if (course.getInstructorId() == null || !course.getInstructorId().equals(instructorId)) {
            throw new BadRequestException("Instructor does not manage this course");
        }
    }
}