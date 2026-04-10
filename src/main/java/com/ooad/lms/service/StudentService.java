package com.ooad.lms.service;

import com.ooad.lms.dto.SubmitAssignmentRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.ProgressTracker;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Student;
import com.ooad.lms.model.Submission;
import com.ooad.lms.model.MaterialFileMetadata;
import org.springframework.core.io.Resource;
import com.ooad.lms.repository.AssignmentRepository;
import com.ooad.lms.repository.CourseRepository;
import com.ooad.lms.repository.MaterialFileMetadataRepository;
import com.ooad.lms.repository.SubmissionRepository;
import com.ooad.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final FileStorageService fileStorageService;
    private final MaterialFileMetadataRepository materialFileMetadataRepository;

    public StudentService(
            CourseRepository courseRepository,
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            UserService userService,
            CourseService courseService,
            FileStorageService fileStorageService,
            MaterialFileMetadataRepository materialFileMetadataRepository
    ) {
        this.courseRepository = courseRepository;
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.fileStorageService = fileStorageService;
        this.materialFileMetadataRepository = materialFileMetadataRepository;
    }

    public Course enroll(Long studentId, Long courseId) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);
        Course course = courseService.getCourse(courseId);
        student.enrollCourse(courseId);
        course.enrollStudent(studentId);
        userRepository.save(student);
        courseRepository.save(course);
        return course;
    }

    public Submission submitAssignment(Long studentId, Long assignmentId, SubmitAssignmentRequest request) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);
        Assignment assignment = courseService.getAssignment(assignmentId);

        boolean enrolled = courseRepository.findAll().stream()
                .filter(course -> course.getAssignments().stream().anyMatch(item -> item.getAssignmentId().equals(assignmentId)))
                .anyMatch(course -> course.getEnrolledStudentIds().contains(studentId));
        if (!enrolled) {
            throw new BadRequestException("Student is not enrolled in the course for this assignment");
        }
        if (assignment.getDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Assignment deadline has passed");
        }

        Submission submission = new Submission(
            null,
                studentId,
                assignmentId,
                LocalDateTime.now(),
                request.content()
        );
        submission.submit();
        submission = submissionRepository.save(submission);
        assignment.evaluate(submission.getSubmissionId());
        assignmentRepository.save(assignment);
        student.submitAssignment(submission.getSubmissionId());
        userRepository.save(student);
        return submission;
    }

    public List<Submission> getGrades(Long studentId) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);
        return student.getSubmissions().stream()
            .map(submissionRepository::findById)
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
                .toList();
    }

    public Map<Long, Double> getProgress(Long studentId) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);

        return student.getEnrolledCourses().stream()
                .map(courseService::getCourse)
                .collect(Collectors.toMap(
                        Course::getCourseId,
                        course -> calculateCourseProgress(studentId, course)
                ));
    }

    public List<String> getDeadlineNotifications(Long studentId) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);
        return student.getEnrolledCourses().stream()
                .map(courseService::getCourse)
                .flatMap(course -> new ProgressTracker(course.getModules().size(), 0)
                        .notifyDeadlines(course.getAssignments()).stream())
                .toList();
    }

    private double calculateCourseProgress(Long studentId, Course course) {
        int totalModules = course.getModules().size();
        if (totalModules == 0) {
            return 0.0;
        }

        long submittedAssignments = course.getAssignments().stream()
                .flatMap(assignment -> assignment.getSubmissions().stream())
            .map(submissionRepository::findById)
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
                .filter(submission -> submission != null && submission.getStudentId().equals(studentId))
                .count();

        int completedModules = (int) Math.min(totalModules, submittedAssignments);
        return new ProgressTracker(totalModules, completedModules).calculateProgress();
    }

    public Submission getSubmission(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found"));
    }

    public MaterialDownload downloadMaterial(Long studentId, Long materialId) {
        userService.validateRole(studentId, Role.STUDENT);

        Course course = courseRepository.findAll().stream()
                .filter(item -> item.getEnrolledStudentIds().contains(studentId))
                .filter(item -> item.getModules().stream()
                        .anyMatch(module -> module.getMaterials().stream()
                                .anyMatch(material -> material.getFileId().equals(materialId))))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Material not found for this student"));

        course.getModules().stream()
                .flatMap(module -> module.getMaterials().stream())
                .filter(material -> material.getFileId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Material not found"));

        MaterialFileMetadata metadata = materialFileMetadataRepository.findById(materialId).orElse(null);
        if (metadata == null) {
            throw new BadRequestException("This material is a link and is not stored as a downloadable file");
        }

        Resource resource = fileStorageService.loadAsResource(metadata.getStoragePath());
        return new MaterialDownload(resource, metadata.getOriginalFileName());
    }

    public record MaterialDownload(Resource resource, String fileName) {
    }
}