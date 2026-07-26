package com.lefkovitzj.sermonarchive.repository;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SermonMediaRepository extends JpaRepository<SermonMedia, Integer> {
    List<SermonMedia> findByTagsContaining(String tag);
    List<SermonMedia> findBySpeakerContaining(String speaker);
    List<SermonMedia> findBySermonDatetimeBetween(LocalDateTime start, LocalDateTime end);
}
