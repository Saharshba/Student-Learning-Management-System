package com.ooad.lms.service;

import com.ooad.lms.model.Course;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Submission;
import com.ooad.lms.model.User;
import com.ooad.lms.repository.InMemoryDataStore;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final UserService userService;
    private final CourseService courseService;
    private final InMemoryDataStore dataStore;

    public AdminService(UserService userService, CourseService courseService, InMemoryDataStore dataStore) {
        this.userService = userService;
        this.courseService = courseService;
        this.dataStore = dataStore;
    }

    public List<User> getAllUsers(Long adminId) {
        userService.validateRole(adminId, Role.ADMINISTRATOR);
        return dataStore.users().values().stream().toList();
    }

    public List<Course> getAllCourses(Long adminId) {
        userService.validateRole(adminId, Role.ADMINISTRATOR);
        return courseService.getAllCourses();
    }

    public Map<String, Object> generateReport(Long adminId) {
        userService.validateRole(adminId, Role.ADMINISTRATOR);

        long totalUsers = dataStore.users().size();
        long totalCourses = dataStore.courses().size();
        long totalStudents = dataStore.users().values().stream().filter(user -> user.getRole() == Role.STUDENT).count();
        long totalInstructors = dataStore.users().values().stream().filter(user -> user.getRole() == Role.INSTRUCTOR).count();
        double averageGrade = dataStore.submissions().values().stream()
                .map(Submission::getGrade)
                .filter(grade -> grade != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalUsers", totalUsers);
        report.put("totalStudents", totalStudents);
        report.put("totalInstructors", totalInstructors);
        report.put("totalCourses", totalCourses);
        report.put("averageGrade", averageGrade);
        report.put("submissionsEvaluated", dataStore.submissions().values().stream().filter(s -> s.getGrade() != null).count());
        return report;
    }
}