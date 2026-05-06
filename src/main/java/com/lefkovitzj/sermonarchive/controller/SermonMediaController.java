package com.lefkovitzj.sermonarchive.controller;

import com.lefkovitzj.sermonarchive.service.SermonMediaService;
import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/sermon-media")
public class SermonMediaController {

    private final SermonMediaService sermonMediaService;

    public SermonMediaController(SermonMediaService sermonMediaService) {
        this.sermonMediaService = sermonMediaService;
    }

    @GetMapping()
    public List<SermonMedia> getSermonMedia() {
        return sermonMediaService.getSermonMedia();
    }

    @PostMapping(value = "/add")
    public ResponseEntity<String> addSermonMedia(
            @ModelAttribute SermonMedia newSermonMedia,
            @RequestParam("sermonFile") MultipartFile sermonFile) {
        sermonMediaService.addSermonMedia(newSermonMedia, sermonFile);
        return ResponseEntity.ok("Sermon media " + newSermonMedia.getTitle() + " by " + newSermonMedia.getSpeaker() + " was added successfully. Uploaded to " + newSermonMedia.getResourceUrl() + ".");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateSermonMedia(@RequestBody SermonMedia updatedSermonMedia) {
        sermonMediaService.updateSermonMedia(updatedSermonMedia);
        return ResponseEntity.ok("Sermon media " + updatedSermonMedia.getTitle() + " by " + updatedSermonMedia.getSpeaker() + " was updated successfully.");
    }
}
