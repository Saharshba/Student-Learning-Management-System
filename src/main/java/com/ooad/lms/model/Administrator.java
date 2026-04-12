package com.ooad.lms.model;

public class Administrator extends User implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public Administrator(Long userId, String name, String email, String password) {
        super(userId, name, email, password, Role.ADMINISTRATOR);
    }
}