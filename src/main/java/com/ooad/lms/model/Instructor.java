package com.ooad.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private final List<Long> coursesHandled = new ArrayList<>();

    public Instructor(Long userId, String name, String email, String password) {
        super(userId, name, email, password, Role.INSTRUCTOR);
    }

    public void assignCourse(Long courseId) {
        if (!coursesHandled.contains(courseId)) {
            coursesHandled.add(courseId);
        }
    }

    public List<Long> getCoursesHandled() {
        return coursesHandled;
    }
}