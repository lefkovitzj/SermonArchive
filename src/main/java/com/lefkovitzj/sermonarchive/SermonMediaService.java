package com.lefkovitzj.sermonarchive;

import org.springframework.stereotype.Service;

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
}
