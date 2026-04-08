package com.ooad.lms.service;

import com.ooad.lms.dto.GradeSubmissionRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Submission;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorService {
    private final UserService userService;
    private final CourseService courseService;
    private final StudentService studentService;

    public InstructorService(UserService userService, CourseService courseService, StudentService studentService) {
        this.userService = userService;
        this.courseService = courseService;
        this.studentService = studentService;
    }

    public List<Submission> getSubmissions(Long instructorId, Long courseId, Long assignmentId) {
        validateInstructorCourseAccess(instructorId, courseId);
        Assignment assignment = courseService.getAssignment(assignmentId);
        return assignment.getSubmissions().stream()
                .map(studentService::getSubmission)
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
        return submission;
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