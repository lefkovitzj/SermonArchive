package com.lefkovitzj.sermonarchive.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;
import java.util.Objects;

@Entity
public class SermonMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private String resourceUrl;
    private String speaker;
    private List<String> tags;
    private boolean isVideo;
    private boolean isPublished;

    public SermonMedia() {
    }

    public SermonMedia(Integer id, String title, String description, String resourceUrl, String speaker, List<String> tags, boolean isVideo, boolean isPublished) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.resourceUrl = resourceUrl;
        this.speaker = speaker;
        this.tags = tags;
        this.isVideo = isVideo;
        this.isPublished = isPublished;
    }

    public Integer getid() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public String getSpeaker() {
        return speaker;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setid(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SermonMedia that = (SermonMedia) o;
        return isVideo == that.isVideo && isPublished == that.isPublished && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(resourceUrl, that.resourceUrl) && Objects.equals(speaker, that.speaker) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, resourceUrl, speaker, tags, isVideo, isPublished);
    }
}
