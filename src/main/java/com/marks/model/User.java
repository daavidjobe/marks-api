package com.marks.model;


import org.hibernate.validator.constraints.Email;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Entity("users")
public class User extends BaseEntity {


    @Email
    @Indexed(options = @IndexOptions(name = "userEmail", unique = true))
    private String email;

    @Embedded
    @Valid
    private List<Mark> marks = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String username;

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", marks=" + marks +
                '}';
    }
}
