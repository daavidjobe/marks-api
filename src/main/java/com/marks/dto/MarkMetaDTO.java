package com.marks.dto;

import com.marks.model.Mark;

import java.util.List;

/**
 * Created by David Jobe on 5/9/16.
 */
public class MarkMetaDTO {
    private List<String> tags;
    private String thumbnail;
    private Mark mark;

    public MarkMetaDTO(List<String> tags, String thumbnail) {
        this.tags = tags;
        this.thumbnail = thumbnail;
    }

    public MarkMetaDTO() {}

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

    @Override
    public String toString() {
        return "MarkMetaDTO{" +
                "tags=" + tags +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
