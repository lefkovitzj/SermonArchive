package com.lefkovitzj.sermonarchive.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
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
    private List<String> tags =  new ArrayList<>();
    private boolean isVideo;
    private boolean isPublished;
    public String s3Key;

    public SermonMedia() {
    }

    public SermonMedia(Integer id, String title, String description, String resourceUrl, String speaker, List<String> tags, boolean isVideo, boolean isPublished, String s3Key) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.resourceUrl = resourceUrl;
        this.speaker = speaker;
        this.tags = tags;
        this.isVideo = isVideo;
        this.isPublished = isPublished;
        this.s3Key = s3Key;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SermonMedia that = (SermonMedia) o;
        return isVideo == that.isVideo && isPublished == that.isPublished && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(resourceUrl, that.resourceUrl) && Objects.equals(speaker, that.speaker) && Objects.equals(tags, that.tags) && Objects.equals(s3Key, that.s3Key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, resourceUrl, speaker, tags, isVideo, isPublished, s3Key);
    }

    public void appendTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    public boolean containsTag(String tag) {
        return this.tags.contains(tag);
    }
}
