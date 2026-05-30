package com.lefkovitzj.sermonarchive.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SpeakerTest {
    @Test
    void toStringShouldReturnNameOnly() {
        Speaker speaker = new Speaker("Test Speaker");

        assertThat(speaker.toString()).isEqualTo("Test Speaker");
    }

    @Test
    void equalsAndHashCodeShouldBeNameBased() {
        Speaker speaker1 = new Speaker("Test Speaker");
        Speaker speaker2 = new Speaker("Test Speaker");
        Speaker speaker3 = new Speaker("Charles Haddon Spurgeon");

        assertThat(speaker1).isEqualTo(speaker2);
        assertThat(speaker1).isNotEqualTo(speaker3);
        assertThat(speaker1).isNotEqualTo(null);

        assertThat(speaker1.hashCode()).isEqualTo(speaker2.hashCode());
        assertThat(speaker1.hashCode()).isNotEqualTo(speaker3.hashCode());
    }
}
