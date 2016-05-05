package com.marks.model;


import org.hibernate.validator.constraints.Email;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Entity("users")
public class User extends BaseEntity {


    @Email
    private String email;

    private String username;

    private String password;

    private String salt;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Embedded
    @Valid
    private List<Category> categories = new ArrayList<>();

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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", categories=" + categories +
                '}';
    }
}
