package com.ooad.lms.designpattern.mvp.homepage;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ooad.lms.model.Course;
import com.ooad.lms.model.Role;
import com.ooad.lms.repository.InMemoryDataStore;
import com.ooad.lms.service.CourseService;

@Component
public class HomePagePresenter {
    private final CourseService courseService;
    private final InMemoryDataStore dataStore;

    public HomePagePresenter(CourseService courseService, InMemoryDataStore dataStore) {
        this.courseService = courseService;
        this.dataStore = dataStore;
    }

    public void present(HomePageView view) {
        List<Course> courses = courseService.getAllCourses();
        long totalStudents = dataStore.users().values().stream().filter(user -> user.getRole() == Role.STUDENT).count();
        long totalInstructors = dataStore.users().values().stream().filter(user -> user.getRole() == Role.INSTRUCTOR).count();
        long totalAdmins = dataStore.users().values().stream().filter(user -> user.getRole() == Role.ADMINISTRATOR).count();
        long totalAssignments = courses.stream().mapToLong(course -> course.getAssignments().size()).sum();

        view.showCourses(courses);
        view.showStats(Map.of(
                "students", totalStudents,
                "instructors", totalInstructors,
                "admins", totalAdmins,
                "courses", (long) courses.size(),
                "assignments", totalAssignments
        ));
        view.showSeededAccounts(List.of(
                Map.of("role", "Administrator", "email", "admin@lms.com", "password", "admin123"),
                Map.of("role", "Instructor", "email", "instructor@lms.com", "password", "teach123"),
                Map.of("role", "Student", "email", "student@lms.com", "password", "learn123")
        ));
    }
}
