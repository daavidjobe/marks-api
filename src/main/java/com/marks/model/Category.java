package com.marks.model;

import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Jobe on 5/6/16.
 */

@Embedded
public class Category {

    private String name;
    private List<String> urls = new ArrayList<>();

    public Category() {}

    public Category(String name, List<String> urls) {
        this.name = name;
        this.urls = urls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> url) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", urls=" + urls +
                '}';
    }
}
