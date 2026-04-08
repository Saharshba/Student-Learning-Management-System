package com.ooad.lms.controller;

import com.ooad.lms.dto.CreateCourseRequest;
import com.ooad.lms.model.Course;
import com.ooad.lms.model.User;
import com.ooad.lms.service.AdminService;
import com.ooad.lms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final CourseService courseService;

    public AdminController(AdminService adminService, CourseService courseService) {
        this.adminService = adminService;
        this.courseService = courseService;
    }

    @GetMapping("/users")
    public List<User> getUsers(@RequestParam Long adminId) {
        return adminService.getAllUsers(adminId);
    }

    @GetMapping("/courses")
    public List<Course> getCourses(@RequestParam Long adminId) {
        return adminService.getAllCourses(adminId);
    }

    @PostMapping("/courses")
    public Course createCourse(@RequestParam Long adminId, @Valid @RequestBody CreateCourseRequest request) {
        adminService.getAllCourses(adminId);
        return courseService.createCourse(request);
    }

    @PutMapping("/courses/{courseId}")
    public Course updateCourse(@PathVariable Long courseId, @RequestParam Long adminId, @Valid @RequestBody CreateCourseRequest request) {
        adminService.getAllCourses(adminId);
        return courseService.updateCourse(courseId, request);
    }

    @DeleteMapping("/courses/{courseId}")
    public void deleteCourse(@PathVariable Long courseId, @RequestParam Long adminId) {
        adminService.getAllCourses(adminId);
        courseService.deleteCourse(courseId);
    }

    @PostMapping("/courses/{courseId}/assign-instructor/{instructorId}")
    public Course assignInstructor(@PathVariable Long courseId, @PathVariable Long instructorId, @RequestParam Long adminId) {
        adminService.getAllCourses(adminId);
        return courseService.assignInstructor(courseId, instructorId);
    }

    @GetMapping("/reports/performance")
    public Map<String, Object> generateReport(@RequestParam Long adminId) {
        return adminService.generateReport(adminId);
    }
}