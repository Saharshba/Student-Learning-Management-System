package com.ooad.lms.designpattern.factory.user;

import com.ooad.lms.dto.RegisterRequest;
import com.ooad.lms.model.Role;
import com.ooad.lms.model.User;

public interface UserRoleFactory {
    Role supportedRole();

    User create(Long userId, RegisterRequest request);
}
