package com.marks.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Jobe on 4/25/16.
 */

@Entity("marks")
public class Mark extends BaseEntity {

    @Property
    @Indexed(IndexDirection.DESC)
    @NotEmpty
    @URL
    private String url;

    private String owner;

    private boolean published = false;

    private List<String> tags = new ArrayList<>();

    private String thumbnail;

    public Mark() {}

    public Mark(String url) {
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "url='" + url + '\'' +
                ", owner='" + owner + '\'' +
                ", published=" + published +
                ", tags=" + tags +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
