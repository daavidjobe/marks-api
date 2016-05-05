package com.marks.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Jobe on 5/5/16.
 */

@Entity("categories")
public class Category extends BaseEntity {

    private String name;
    private List<ObjectId> markIds = new ArrayList<>();

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectId> getMarkIds() {
        return markIds;
    }

    public void setMarkIds(List<ObjectId> markIds) {
        this.markIds = markIds;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", markIds=" + markIds +
                '}';
    }
}
