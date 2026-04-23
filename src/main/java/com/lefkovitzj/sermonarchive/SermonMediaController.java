package com.lefkovitzj.sermonarchive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add")
    public ResponseEntity<String> addSermonMedia(@RequestBody SermonMedia newSermonMedia) {
        sermonMediaService.addSermonMedia(newSermonMedia);
        return ResponseEntity.ok("Sermon media " + newSermonMedia.getTitle() + " by " + newSermonMedia.getSpeaker() + " was added successfully.");
    }
}
