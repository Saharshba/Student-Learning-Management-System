package com.ooad.lms.service;

import com.ooad.lms.dto.LoginRequest;
import com.ooad.lms.dto.RegisterRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.exception.NotFoundException;
import com.ooad.lms.model.Administrator;
import com.ooad.lms.model.Instructor;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.Student;
import com.ooad.lms.model.User;
import com.ooad.lms.repository.InMemoryDataStore;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final InMemoryDataStore dataStore;

    public UserService(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public User register(RegisterRequest request) {
        boolean emailExists = dataStore.users().values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(request.email()));
        if (emailExists) {
            throw new BadRequestException("Email already registered");
        }

        long userId = dataStore.nextUserId();
        User user = switch (request.role()) {
            case STUDENT -> new Student(userId, request.name(), request.email(), request.password());
            case INSTRUCTOR -> new Instructor(userId, request.name(), request.email(), request.password());
            case ADMINISTRATOR -> new Administrator(userId, request.name(), request.email(), request.password());
        };

        dataStore.users().put(userId, user);
        return user;
    }

    public User login(LoginRequest request) {
        return dataStore.users().values().stream()
                .filter(user -> user.login(request.email(), request.password()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Invalid credentials"));
    }

    public User getUser(Long userId) {
        User user = dataStore.users().get(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    public void validateRole(Long userId, Role role) {
        User user = getUser(userId);
        if (user.getRole() != role) {
            throw new BadRequestException("User does not have required role: " + role);
        }
    }
}