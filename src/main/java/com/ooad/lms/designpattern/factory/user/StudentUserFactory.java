package com.ooad.lms.designpattern.factory.user;

import org.springframework.stereotype.Component;

import com.ooad.lms.dto.RegisterRequest;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Student;
import com.ooad.lms.model.User;

@Component
public class StudentUserFactory implements UserRoleFactory {
    @Override
    public Role supportedRole() {
        return Role.STUDENT;
    }

    @Override
    public User create(Long userId, RegisterRequest request) {
        return new Student(userId, request.name(), request.email(), request.password());
    }
}
