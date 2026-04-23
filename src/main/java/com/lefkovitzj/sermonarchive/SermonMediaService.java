package com.lefkovitzj.sermonarchive;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SermonMediaService {
    private final SermonMediaRepository sermonMediaRepository;

    public SermonMediaService(SermonMediaRepository sermonMediaRepository) {
        this.sermonMediaRepository = sermonMediaRepository;
    }

    public List<SermonMedia> getSermonMedia() {
        return sermonMediaRepository.findAll();
    }

    @Transactional
    public void addSermonMedia(SermonMedia sermonMedia) {
        sermonMediaRepository.save(sermonMedia);
    }

    @Transactional
    public void updateSermonMedia(SermonMedia updatedSermonMedia) {
        sermonMediaRepository.save(updatedSermonMedia);
    }
}
