package com.lefkovitzj.sermonarchive.config;

import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.service.SpeakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSpeakerConverter implements Converter<String, Speaker> {
    @Autowired
    private SpeakerService speakerService;

    @Override
    public Speaker convert(String speakerName) {
        if (speakerName == null || speakerName.isEmpty()) return null;
        if (speakerService.getSpeakerByName(speakerName) != null) {
            return speakerService.getSpeakerByName(speakerName);
        }
        return new Speaker(speakerName.trim());
    }

}
