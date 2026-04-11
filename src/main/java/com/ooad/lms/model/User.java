package com.ooad.lms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class User implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String name;
    private String email;
    private String password;
    private Role role;

    protected User(Long userId, String name, String email, String password, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean login(String email, String password) {
        return this.email.equalsIgnoreCase(email) && this.password.equals(password);
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}