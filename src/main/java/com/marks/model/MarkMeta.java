package com.marks.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Jobe on 5/12/16.
 */
@Entity("marksMeta")
public class MarkMeta extends BaseEntity {

    @NotEmpty
    @URL
    private String url;

    @Property("promotions")
    private int promotions = 0;

    @Property("demotions")
    private int demotions = 0;

    private List<String> userEmails = new ArrayList<>();

    public MarkMeta() {}

    public MarkMeta(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPromotions() {
        return promotions;
    }

    public void setPromotions(int promotions) {
        this.promotions = promotions;
    }

    public int getDemotions() {
        return demotions;
    }

    public void setDemotions(int demotions) {
        this.demotions = demotions;
    }

    public List<String> getUserEmails() {
        return userEmails;
    }

    public void setUserEmails(List<String> userEmails) {
        this.userEmails = userEmails;
    }

    @Override
    public String toString() {
        return "MarkMeta{" +
                "url='" + url + '\'' +
                ", promotions=" + promotions +
                ", demotions=" + demotions +
                ", userEmails=" + userEmails +
                '}';
    }
}
