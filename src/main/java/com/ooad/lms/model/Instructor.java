package com.ooad.lms.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends User {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "instructor_courses", joinColumns = @JoinColumn(name = "instructor_id"))
    @Column(name = "course_id")
    private final List<Long> coursesHandled = new ArrayList<>();

    protected Instructor() {
    }

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