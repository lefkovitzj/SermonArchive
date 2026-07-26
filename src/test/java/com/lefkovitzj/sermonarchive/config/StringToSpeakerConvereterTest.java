package com.lefkovitzj.sermonarchive.config;

import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.service.SpeakerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StringToSpeakerConvereterTest {
    @Mock
    private SpeakerService speakerService;

    @InjectMocks
    private StringToSpeakerConverter converter;

    @Test
    void shouldReturnNullWhenInputNullOrEmpty() {
        assertThat(converter.convert("")).isNull();
        assertThat(converter.convert(null)).isNull();
    }

    @Test
    void shouldReturnExistingSpeakerWhenFound() {
        String name = "John Doe";
        Speaker existingSpeaker = new Speaker(name);

        when(speakerService.getSpeakerByName(name)).thenReturn(existingSpeaker);

        Speaker result = converter.convert(name);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(existingSpeaker);
    }

    @Test
    void shouldReturnNewWhenSpeakerNotFound() {
        String name = " Jane Doe ";
        String trimmed_name = "Jane Doe";

        when(speakerService.getSpeakerByName(name)).thenReturn(null);
        Speaker result = converter.convert(name);


        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(trimmed_name);
    }
}
