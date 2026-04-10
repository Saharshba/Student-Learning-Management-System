package com.ooad.lms.service;

import org.springframework.stereotype.Component;

import com.ooad.lms.dto.CreateCourseRequest;
import com.ooad.lms.dto.CreateExamRequest;
import com.ooad.lms.dto.CreateModuleRequest;
import com.ooad.lms.dto.RegisterRequest;
import com.ooad.lms.dto.UploadMaterialRequest;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.MaterialType;
import com.ooad.lms.model.Role;

import jakarta.annotation.PostConstruct;

@Component
public class DataSeeder {
    private final UserService userService;
    private final CourseService courseService;

    public DataSeeder(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @PostConstruct
    public void seed() {
        if (userService.countUsers() > 0) {
            return;
        }

        userService.register(new RegisterRequest("System Admin", "admin@lms.com", "admin123", Role.ADMINISTRATOR));
        var instructor = userService.register(new RegisterRequest("Course Instructor", "instructor@lms.com", "teach123", Role.INSTRUCTOR));
        userService.register(new RegisterRequest("Student User", "student@lms.com", "learn123", Role.STUDENT));

        Course course = courseService.createCourse(new CreateCourseRequest("OOAD Fundamentals", "Core concepts of object-oriented analysis and design."));
        courseService.assignInstructor(course.getCourseId(), instructor.getUserId());
        var module = courseService.addModule(instructor.getUserId(), course.getCourseId(), new CreateModuleRequest("Introduction to LMS"));
        courseService.addMaterial(
                instructor.getUserId(),
                course.getCourseId(),
                module.getModuleId(),
                new UploadMaterialRequest("LMS Overview", MaterialType.PDF, "https://example.com/lms-overview.pdf")
        );
        courseService.addExam(
                instructor.getUserId(),
                course.getCourseId(),
                new CreateExamRequest(
                        "Midterm Exam",
                        "Midterm timetable for OOAD Fundamentals. Download the PDF or review the image schedule.",
                        "2026-05-10T10:00",
                        MaterialType.PDF,
                        "https://example.com/ooad-midterm.pdf"
                )
        );
    }
}