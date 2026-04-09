package com.ooad.lms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ooad.lms.dto.CreateAssignmentRequest;
import com.ooad.lms.dto.CreateCourseRequest;
import com.ooad.lms.dto.CreateExamRequest;
import com.ooad.lms.dto.CreateModuleRequest;
import com.ooad.lms.dto.UploadMaterialRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Assignment;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.Exam;
import com.ooad.lms.model.Instructor;
import com.ooad.lms.model.Material;
import com.ooad.lms.model.Module;
import com.ooad.lms.model.Role;
import com.ooad.lms.repository.InMemoryDataStore;

@Service
public class CourseService {
    private final InMemoryDataStore dataStore;
    private final UserService userService;

    public CourseService(InMemoryDataStore dataStore, UserService userService) {
        this.dataStore = dataStore;
        this.userService = userService;
    }

    public Course createCourse(CreateCourseRequest request) {
        long courseId = dataStore.nextCourseId();
        Course course = new Course(courseId, request.title(), request.description());
        dataStore.courses().put(courseId, course);
        return course;
    }

    public List<Course> getAllCourses() {
        return dataStore.courses().values().stream().toList();
    }

    public Course getCourse(Long courseId) {
        Course course = dataStore.courses().get(courseId);
        if (course == null) {
            throw new NotFoundException("Course not found");
        }
        return course;
    }

    public Course updateCourse(Long courseId, CreateCourseRequest request) {
        Course course = getCourse(courseId);
        Course updatedCourse = new Course(course.getCourseId(), request.title(), request.description());
        updatedCourse.setInstructorId(course.getInstructorId());
        updatedCourse.getModules().addAll(course.getModules());
        updatedCourse.getAssignments().addAll(course.getAssignments());
        updatedCourse.getEnrolledStudentIds().addAll(course.getEnrolledStudentIds());
        dataStore.courses().put(courseId, updatedCourse);
        return updatedCourse;
    }

    public void deleteCourse(Long courseId) {
        if (dataStore.courses().remove(courseId) == null) {
            throw new NotFoundException("Course not found");
        }
    }

    public Course assignInstructor(Long courseId, Long instructorId) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);
        Course course = getCourse(courseId);
        course.setInstructorId(instructorId);
        Instructor instructor = (Instructor) userService.getUser(instructorId);
        instructor.assignCourse(courseId);
        return course;
    }

    public Module addModule(Long instructorId, Long courseId, CreateModuleRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Module module = new Module(dataStore.nextModuleId(), request.title());
        getCourse(courseId).addModule(module);
        return module;
    }

    public Material addMaterial(Long instructorId, Long courseId, Long moduleId, UploadMaterialRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Module module = getCourse(courseId).getModules().stream()
                .filter(existing -> existing.getModuleId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Module not found"));

        Material material = new Material(
                dataStore.nextMaterialId(),
                request.fileType(),
                LocalDateTime.now(),
                request.name(),
                request.contentUrl()
        );
        module.addMaterial(material);
        return material;
    }

    public Assignment createAssignment(Long instructorId, Long courseId, CreateAssignmentRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Assignment assignment = new Assignment(dataStore.nextAssignmentId(), request.description(), request.deadline());
        assignment.publish();
        getCourse(courseId).addAssignment(assignment);
        dataStore.assignments().put(assignment.getAssignmentId(), assignment);
        return assignment;
    }

    public Exam addExam(Long instructorId, Long courseId, CreateExamRequest request) {
        validateInstructorForCourse(instructorId, courseId);
        Exam exam = new Exam(
                dataStore.nextExamId(),
                request.title(),
                request.description(),
                java.time.LocalDateTime.parse(request.scheduleDateTime()),
                request.fileType(),
                request.fileUrl(),
                java.time.LocalDateTime.now()
        );
        getCourse(courseId).addExam(exam);
        return exam;
    }

    public Assignment getAssignment(Long assignmentId) {
        Assignment assignment = dataStore.assignments().get(assignmentId);
        if (assignment == null) {
            throw new NotFoundException("Assignment not found");
        }
        return assignment;
    }

    private void validateInstructorForCourse(Long instructorId, Long courseId) {
        userService.validateRole(instructorId, Role.INSTRUCTOR);
        Course course = getCourse(courseId);
        if (course.getInstructorId() == null || !course.getInstructorId().equals(instructorId)) {
            throw new BadRequestException("Instructor is not assigned to this course");
        }
    }
}