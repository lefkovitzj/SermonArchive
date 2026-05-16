package com.lefkovitzj.sermonarchive.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class SermonMedia {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Getter
    private String title;

    @Setter
    @Getter
    private String description;

    @Setter
    @Getter
    private String resourceUrl;

    @Setter
    @Getter
    private String speaker;

    @Setter
    @Getter
    private List<String> tags =  new ArrayList<>();

    @Setter
    @Getter
    private boolean isVideo;

    @Setter
    @Getter
    private boolean isPublished;

    @Setter
    @Getter
    private LocalDateTime sermonDatetime;

    @Setter
    @Getter
    public String s3Key;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


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
