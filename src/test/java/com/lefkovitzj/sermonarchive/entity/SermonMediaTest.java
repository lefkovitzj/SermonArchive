package com.lefkovitzj.sermonarchive.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SermonMediaTest {
    @Test
    void appendTagShouldAppendTag() {
        SermonMedia sermonMedia = new SermonMedia();
        sermonMedia.appendTag("tag1");
        sermonMedia.appendTag("tag2");

        assertThat(sermonMedia.getTags()).isEqualTo(new ArrayList<>(List.of("tag1", "tag2")));
    }
    @Test
    void addDuplicateTagShouldSkip() {
        SermonMedia sermonMedia = new SermonMedia();
        sermonMedia.appendTag("tag1");
        sermonMedia.appendTag("tag2");
        sermonMedia.appendTag("tag1");

        assertThat(sermonMedia.getTags()).isEqualTo(new ArrayList<>(List.of("tag1", "tag2")));
    }

    @Test
    void removeTagShouldDelete() {
        SermonMedia sermonMedia = new SermonMedia();
        sermonMedia.appendTag("tag1");
        sermonMedia.appendTag("tag2");
        sermonMedia.removeTag("tag1");

        assertThat(sermonMedia.getTags()).isEqualTo(new ArrayList<>(List.of("tag2")));
    }

    @Test
    void removeNonexistentTagShouldNotDelete() {
        SermonMedia sermonMedia = new SermonMedia();
        sermonMedia.appendTag("tag1");
        sermonMedia.removeTag("tag2");

        assertThat(sermonMedia.getTags()).isEqualTo(new ArrayList<>(List.of("tag1")));
    }

    @Test
    void containsTagShouldReturnFalseIfMissing() {
        SermonMedia sermonMedia = new SermonMedia();
        sermonMedia.appendTag("tag1");

        assertThat(sermonMedia.containsTag("tag2")).isFalse();
    }

    @Test
    void containsTagShouldReturnTrueIfExists() {
        SermonMedia sermonMedia = new SermonMedia();
        sermonMedia.appendTag("tag1");

        assertThat(sermonMedia.containsTag("tag1")).isTrue();
    }

    @Test
    void addAndRemoveShouldStartFreshIfTagsNull() {
        SermonMedia sermonMedia = new SermonMedia();
        sermonMedia.setTags(null);
        sermonMedia.appendTag("tag1");

        assertThat(sermonMedia.getTags()).isNotNull();

        sermonMedia.setTags(null);
        sermonMedia.removeTag("tag1");
        assertThat(sermonMedia.getTags()).isNotNull();
    }
}
