package com.marks.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * Created by David Jobe on 4/25/16.
 */

@Entity(value = "marks")
public class Mark extends BaseEntity {

    @Property
    @Indexed(IndexDirection.DESC)
    @NotEmpty
    @URL
    private String url;

    private boolean published = false;

    private int popularityLevel = 1;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPopularityLevel() {
        return popularityLevel;
    }

    public void setPopularityLevel(int popularityLevel) {
        this.popularityLevel = popularityLevel;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "url='" + url + '\'' +
                ", published=" + published +
                ", popularityLevel=" + popularityLevel +
                '}';
    }
}
