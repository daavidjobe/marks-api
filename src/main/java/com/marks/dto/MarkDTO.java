package com.marks.dto;

import com.marks.model.Mark;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by David Jobe on 5/9/16.
 */
public class MarkDTO {
    private List<String> tags;
    private String thumbnail;
    private Mark mark;
    private int promotions;
    private int demotions;
    private String url;
    private Timestamp creationDate;
    private boolean hasInteracted;

    public MarkDTO(List<String> tags, String thumbnail) {
        this.tags = tags;
        this.thumbnail = thumbnail;
    }

    public MarkDTO(String url, String thumbnail, Timestamp creationDate,
                   int promotions, int demotions, boolean hasInteracted) {
        this.url = url;
        this.thumbnail = thumbnail;
        this.creationDate = creationDate;
        this.promotions = promotions;
        this.demotions = demotions;
        this.hasInteracted = hasInteracted;
    }

    public MarkDTO() {}

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

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "MarkDTO{" +
                "tags=" + tags +
                ", thumbnail='" + thumbnail + '\'' +
                ", mark=" + mark +
                ", promotions=" + promotions +
                ", demotions=" + demotions +
                ", url='" + url + '\'' +
                ", creationDate=" + creationDate +
                ", hasInteracted=" + hasInteracted +
                '}';
    }
}
