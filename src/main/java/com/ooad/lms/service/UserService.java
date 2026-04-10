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
import com.ooad.lms.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        User user = switch (request.role()) {
            case STUDENT -> new Student(null, request.name(), request.email(), request.password());
            case INSTRUCTOR -> new Instructor(null, request.name(), request.email(), request.password());
            case ADMINISTRATOR -> new Administrator(null, request.name(), request.email(), request.password());
        };

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        return userRepository.findByEmailIgnoreCaseAndPassword(request.email(), request.password())
                .orElseThrow(() -> new NotFoundException("Invalid credentials"));
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void validateRole(Long userId, Role role) {
        User user = getUser(userId);
        if (user.getRole() != role) {
            throw new BadRequestException("User does not have required role: " + role);
        }
    }

    public long countUsers() {
        return userRepository.count();
    }
}