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
import com.ooad.lms.repository.InMemoryDataStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final InMemoryDataStore dataStore;
    private final UserService userService;
    private final CourseService courseService;

    public StudentService(InMemoryDataStore dataStore, UserService userService, CourseService courseService) {
        this.dataStore = dataStore;
        this.userService = userService;
        this.courseService = courseService;
    }

    public Course enroll(Long studentId, Long courseId) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);
        Course course = courseService.getCourse(courseId);
        student.enrollCourse(courseId);
        course.enrollStudent(studentId);
        return course;
    }

    public Submission submitAssignment(Long studentId, Long assignmentId, SubmitAssignmentRequest request) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);
        Assignment assignment = courseService.getAssignment(assignmentId);

        boolean enrolled = dataStore.courses().values().stream()
                .filter(course -> course.getAssignments().stream().anyMatch(item -> item.getAssignmentId().equals(assignmentId)))
                .anyMatch(course -> course.getEnrolledStudentIds().contains(studentId));
        if (!enrolled) {
            throw new BadRequestException("Student is not enrolled in the course for this assignment");
        }
        if (assignment.getDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Assignment deadline has passed");
        }

        Submission submission = new Submission(
                dataStore.nextSubmissionId(),
                studentId,
                assignmentId,
                LocalDateTime.now(),
                request.content()
        );
        submission.submit();
        dataStore.submissions().put(submission.getSubmissionId(), submission);
        assignment.evaluate(submission.getSubmissionId());
        student.submitAssignment(submission.getSubmissionId());
        return submission;
    }

    public List<Submission> getGrades(Long studentId) {
        userService.validateRole(studentId, Role.STUDENT);
        Student student = (Student) userService.getUser(studentId);
        return student.getSubmissions().stream()
                .map(submissionId -> dataStore.submissions().get(submissionId))
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
                .map(submissionId -> dataStore.submissions().get(submissionId))
                .filter(submission -> submission != null && submission.getStudentId().equals(studentId))
                .count();

        int completedModules = (int) Math.min(totalModules, submittedAssignments);
        return new ProgressTracker(totalModules, completedModules).calculateProgress();
    }

    public Submission getSubmission(Long submissionId) {
        Submission submission = dataStore.submissions().get(submissionId);
        if (submission == null) {
            throw new NotFoundException("Submission not found");
        }
        return submission;
    }
}