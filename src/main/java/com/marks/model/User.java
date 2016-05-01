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

    private String username;

    private String password;

    private String salt;

    @Embedded
    @Valid
    private List<Mark> marks = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", marks=" + marks +
                '}';
    }
}
