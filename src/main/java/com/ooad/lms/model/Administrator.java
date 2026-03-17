package com.ooad.lms.model;

public class Administrator extends User {

    public Administrator(Long userId, String name, String email, String password) {
        super(userId, name, email, password, Role.ADMINISTRATOR);
    }
}