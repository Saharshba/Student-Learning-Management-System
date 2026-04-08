package com.ooad.lms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ooad.lms.model.Course;
import com.ooad.lms.model.Role;
import com.ooad.lms.repository.InMemoryDataStore;
import com.ooad.lms.service.CourseService;

@Controller
public class WebController {
    private final CourseService courseService;
    private final InMemoryDataStore dataStore;

    public WebController(CourseService courseService, InMemoryDataStore dataStore) {
        this.courseService = courseService;
        this.dataStore = dataStore;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard/student")
    public String studentDashboard() {
        return "student";
    }

    @GetMapping("/dashboard/instructor")
    public String instructorDashboard() {
        return "instructor";
    }

    @GetMapping("/dashboard/admin")
    public String adminDashboard() {
        return "admin";
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Course> courses = courseService.getAllCourses();
        long totalStudents = dataStore.users().values().stream().filter(user -> user.getRole() == Role.STUDENT).count();
        long totalInstructors = dataStore.users().values().stream().filter(user -> user.getRole() == Role.INSTRUCTOR).count();
        long totalAdmins = dataStore.users().values().stream().filter(user -> user.getRole() == Role.ADMINISTRATOR).count();
        long totalAssignments = courses.stream().mapToLong(course -> course.getAssignments().size()).sum();

        model.addAttribute("courses", courses);
        model.addAttribute("stats", Map.of(
                "students", totalStudents,
                "instructors", totalInstructors,
                "admins", totalAdmins,
                "courses", courses.size(),
                "assignments", totalAssignments
        ));
        model.addAttribute("seededAccounts", List.of(
                Map.of("role", "Administrator", "email", "admin@lms.com", "password", "admin123"),
                Map.of("role", "Instructor", "email", "instructor@lms.com", "password", "teach123"),
                Map.of("role", "Student", "email", "student@lms.com", "password", "learn123")
        ));
        return "index";
    }
}