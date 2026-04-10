package com.ooad.lms.service;

import com.ooad.lms.model.Course;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Submission;
import com.ooad.lms.model.User;
import com.ooad.lms.repository.CourseRepository;
import com.ooad.lms.repository.SubmissionRepository;
import com.ooad.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final UserService userService;
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;

    public AdminService(
            UserService userService,
            CourseService courseService,
            UserRepository userRepository,
            CourseRepository courseRepository,
            SubmissionRepository submissionRepository
    ) {
        this.userService = userService;
        this.courseService = courseService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.submissionRepository = submissionRepository;
    }

    public List<User> getAllUsers(Long adminId) {
        userService.validateRole(adminId, Role.ADMINISTRATOR);
        return userRepository.findAll();
    }

    public List<Course> getAllCourses(Long adminId) {
        userService.validateRole(adminId, Role.ADMINISTRATOR);
        return courseService.getAllCourses();
    }

    public Map<String, Object> generateReport(Long adminId) {
        userService.validateRole(adminId, Role.ADMINISTRATOR);

        long totalUsers = userRepository.count();
        long totalCourses = courseRepository.count();
        List<User> users = userRepository.findAll();
        List<Submission> submissions = submissionRepository.findAll();
        long totalStudents = users.stream().filter(user -> user.getRole() == Role.STUDENT).count();
        long totalInstructors = users.stream().filter(user -> user.getRole() == Role.INSTRUCTOR).count();
        double averageGrade = submissions.stream()
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
        report.put("submissionsEvaluated", submissions.stream().filter(s -> s.getGrade() != null).count());
        return report;
    }
}