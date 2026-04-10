package com.ooad.lms.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends User {

    protected Administrator() {
    }

    public Administrator(Long userId, String name, String email, String password) {
        super(userId, name, email, password, Role.ADMINISTRATOR);
    }
}