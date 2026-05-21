package com.lefkovitzj.sermonarchive.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SermonMedia {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Internal object ID representing the sermon media.")
    private Integer id;

    @Setter
    @Getter
    @Schema(description = "The title of the sermon.")
    private String title;

    @Setter
    @Getter
    @Schema(description = "The full-length description of the sermon.")
    private String description;

    @Setter
    @Getter
    @Schema(description = "The S3 object store public access URL for the sermon, if S3 is configured for public access.")
    private String resourceUrl;

    @Setter
    @Getter
    @Schema(description = "The speaker who preached the sermon.")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Speaker speaker;

    @Setter
    @Getter
    @Schema(description = "The church where the sermon was preached, as determined by the church it was uploaded by.")
    @ManyToOne(cascade = CascadeType.ALL)
    private Church church;

    @Setter
    @Getter
    @Schema(description = "A list of tags associated with the sermon.")
    private List<String> tags =  new ArrayList<>();

    @Setter
    @Getter
    @Schema(description = "Media is a video if true, audio otherwise.")
    private boolean isVideo;

    @Setter
    @Getter
    @Schema(description = "Media is a public if true, private otherwise.")
    private boolean isPublished;

    @Setter
    @Getter
    @Schema(description = "The time at which the sermon was preached.")
    private LocalDateTime sermonDatetime;

    @Setter
    @Getter
    @Schema(description = "The media access key for the sermon in the S3 object store.")
    private String s3Key;

    @CreationTimestamp
    @Schema(description = "Internal system upload time.")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Schema(description = "Internal system lastmod time.")
    private LocalDateTime updatedAt;

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
