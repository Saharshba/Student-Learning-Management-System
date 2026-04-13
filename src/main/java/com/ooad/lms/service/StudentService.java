package com.ooad.lms.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ooad.lms.dto.NotificationDTO;
import com.ooad.lms.dto.SubmissionViewResponse;
import com.ooad.lms.dto.SubmitAssignmentRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.MaterialFileMetadata;
import com.ooad.lms.model.ProgressTracker;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Student;
import com.ooad.lms.model.Submission;
import com.ooad.lms.model.SubmissionFileMetadata;
import com.ooad.lms.repository.AssignmentRepository;
import com.ooad.lms.repository.CourseRepository;
import com.ooad.lms.repository.MaterialFileMetadataRepository;
import com.ooad.lms.repository.SubmissionFileMetadataRepository;
import com.ooad.lms.repository.SubmissionRepository;
import com.ooad.lms.repository.UserRepository;

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
    private final SubmissionFileMetadataRepository submissionFileMetadataRepository;
    private final NotificationService notificationService;

    public StudentService(
            CourseRepository courseRepository,
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            UserService userService,
            CourseService courseService,
            FileStorageService fileStorageService,
                MaterialFileMetadataRepository materialFileMetadataRepository,
                SubmissionFileMetadataRepository submissionFileMetadataRepository,
                NotificationService notificationService
    ) {
        this.courseRepository = courseRepository;
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.fileStorageService = fileStorageService;
        this.materialFileMetadataRepository = materialFileMetadataRepository;
        this.submissionFileMetadataRepository = submissionFileMetadataRepository;
        this.notificationService = notificationService;
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
        Assignment assignment = validateStudentCanSubmit(studentId, assignmentId);
        String content = request.content();

        Submission submission = saveSubmission(studentId, assignment, content);
        updateStudentSubmissionMap(studentId, submission.getSubmissionId());
        return submission;
    }

    public Submission submitAssignmentPdf(Long studentId, Long assignmentId, MultipartFile file, String content) {
        Assignment assignment = validateStudentCanSubmit(studentId, assignmentId);
        FileStorageService.StoredFile storedFile = fileStorageService.storePdf(file);

        // Store only the student's optional notes as content; the actual PDF is tracked via SubmissionFileMetadata
        String submissionText = (content != null && !content.isBlank()) ? content : "";

        Submission submission = saveSubmission(studentId, assignment, submissionText);

        submissionFileMetadataRepository.save(new SubmissionFileMetadata(
                submission.getSubmissionId(),
                studentId,
                storedFile.storagePath(),
                storedFile.originalFileName()
        ));

        updateStudentSubmissionMap(studentId, submission.getSubmissionId());
        return submission;
    }

    private Assignment validateStudentCanSubmit(Long studentId, Long assignmentId) {
        userService.validateRole(studentId, Role.STUDENT);
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

        return assignment;
    }

    private Submission saveSubmission(Long studentId, Assignment assignment, String content) {
        Submission submission = new Submission(
                null,
                studentId,
                assignment.getAssignmentId(),
                LocalDateTime.now(),
                content
        );
        submission.submit();
        submission = submissionRepository.save(submission);
        assignment.evaluate(submission.getSubmissionId());
        assignmentRepository.save(assignment);
        return submission;
    }

    private void updateStudentSubmissionMap(Long studentId, Long submissionId) {
        Student student = (Student) userService.getUser(studentId);
        student.submitAssignment(submissionId);
        userRepository.save(student);
    }

    public List<SubmissionViewResponse> getGrades(Long studentId) {
        userService.validateRole(studentId, Role.STUDENT);
        return submissionRepository.findByStudentIdOrderByTimestampDesc(studentId).stream()
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
        List<String> notifications = student.getEnrolledCourses().stream()
                .map(courseService::getCourse)
                .flatMap(course -> new ProgressTracker(course.getModules().size(), 0)
                        .notifyDeadlines(course.getAssignments()).stream())
            .toList();

        List<String> commentNotifications = notificationService.getMessagesForUser(studentId);
        notifications = new java.util.ArrayList<>(notifications);
        notifications.addAll(commentNotifications);
        return notifications;
    }

    public List<NotificationDTO> getNotifications(Long studentId) {
        userService.validateRole(studentId, Role.STUDENT);
        List<NotificationDTO> commentNotifications = notificationService.getNotificationsForUser(studentId);
        return commentNotifications;
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

    public MaterialDownload downloadSubmittedAssignmentPdf(Long studentId, Long submissionId) {
        userService.validateRole(studentId, Role.STUDENT);

        SubmissionFileMetadata metadata = submissionFileMetadataRepository
                .findBySubmissionIdAndStudentId(submissionId, studentId)
                .orElseThrow(() -> new NotFoundException("Submitted file not found"));

        Resource resource = fileStorageService.loadAsResource(metadata.getStoragePath());
        return new MaterialDownload(resource, metadata.getOriginalFileName());
    }

    public void validateStudentAssignmentAccess(Long studentId, Long assignmentId) {
        userService.validateRole(studentId, Role.STUDENT);

        boolean canAccess = courseRepository.findAll().stream()
                .filter(course -> course.getEnrolledStudentIds().contains(studentId))
                .anyMatch(course -> course.getAssignments().stream()
                        .anyMatch(assignment -> assignment.getAssignmentId().equals(assignmentId)));

        if (!canAccess) {
            throw new NotFoundException("Assignment not found for this student");
        }
    }

    public record MaterialDownload(Resource resource, String fileName) {
    }
}