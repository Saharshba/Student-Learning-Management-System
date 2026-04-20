package com.ooad.lms.designpattern.factory.user;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ooad.lms.dto.RegisterRequest;
import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.User;

@Component
public class UserFactoryRegistry {
    private final Map<Role, UserRoleFactory> roleFactories = new EnumMap<>(Role.class);

    public UserFactoryRegistry(List<UserRoleFactory> factories) {
        for (UserRoleFactory factory : factories) {
            roleFactories.put(factory.supportedRole(), factory);
        }
    }

    public User createUser(Long userId, RegisterRequest request) {
        UserRoleFactory factory = roleFactories.get(request.role());
        if (factory == null) {
            throw new BadRequestException("Unsupported role: " + request.role());
        }
        return factory.create(userId, request);
    }
}
