package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service public class SpeakerService {
    @Autowired
    private SpeakerRepository speakerRepository;

    public Speaker getSpeakerByName(String speakerName) {
        return speakerRepository.findByName(speakerName);
    }

}
