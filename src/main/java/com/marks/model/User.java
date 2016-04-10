package com.marks.model;

import org.mongodb.morphia.annotations.Entity;

@Entity("users")
public class User extends BaseEntity {

    private String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
