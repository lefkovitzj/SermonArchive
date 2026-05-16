package com.lefkovitzj.sermonarchive.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class SermonMedia {
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
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Speaker speaker;

    @Setter
    @Getter
    @ManyToOne(cascade = CascadeType.ALL)
    private Church church;

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

    public SermonMedia(Integer id, String title, String description, String resourceUrl, Speaker speaker, List<String> tags, boolean isVideo, boolean isPublished, String s3Key) {
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
        return isVideo == that.isVideo && isPublished == that.isPublished && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(resourceUrl, that.resourceUrl) && Objects.equals(speaker, that.speaker) && Objects.equals(church, that.church) && Objects.equals(tags, that.tags) && Objects.equals(sermonDatetime, that.sermonDatetime) && Objects.equals(s3Key, that.s3Key) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, resourceUrl, speaker, church, tags, isVideo, isPublished, sermonDatetime, s3Key, createdAt, updatedAt);
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
