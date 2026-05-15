package com.lefkovitzj.sermonarchive.controller;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.service.SermonMediaService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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
        if (!sermonMediaService.isMedia(sermonFile)) {
            return ResponseEntity.badRequest().body("Invalid sermon media file type (" + sermonMediaService.getExt(sermonFile) + ")");
        }
        if (sermonMediaService.isVideo(sermonFile)) {
            // A video file.
            newSermonMedia.setVideo(true);
        }
        else {
            // An audio file.
            newSermonMedia.setVideo(false);
        }

        // Upload the file.
        sermonMediaService.addSermonMedia(newSermonMedia, sermonFile);
        return ResponseEntity.ok("Sermon media '" + newSermonMedia.getTitle() + "' of type " + (newSermonMedia.isVideo() ? "video" : "audio") +  " by " + newSermonMedia.getSpeaker() + " was added successfully. Uploaded to " + newSermonMedia.getResourceUrl() + ".");
    }

    @GetMapping("/download/{sermonId}")
    public ResponseEntity<InputStreamResource> downloadSermon(@PathVariable Integer sermonId) {
        SermonMedia sermonMedia = sermonMediaService.getSermonMediaById(sermonId);
        // Check that the sermon was accessible (exists and published).
        if (sermonMedia == null) {
            return ResponseEntity.notFound().build();
        }
        // Get the bytes and format them for the response.
        InputStream mediaStream = sermonMediaService.getFileForDownload(sermonMedia);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sermonMedia.getTitle() + "\"")
                .body(new InputStreamResource(mediaStream));
    }

    @GetMapping("/embed/{sermonId}")
    public ResponseEntity<InputStreamResource> embedSermon(@PathVariable Integer sermonId) {
        SermonMedia sermonMedia = sermonMediaService.getSermonMediaById(sermonId);
        // Check that the sermon was accessible (exists and published).
        if (sermonMedia == null) {
            return ResponseEntity.notFound().build();
        }
        // Get the bytes and format them for the response.
        InputStream mediaStream = sermonMediaService.getFileForDownload(sermonMedia);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sermonMedia.getTitle() + "\"")
                .body(new InputStreamResource(mediaStream));
    }

    @PutMapping("/publish/{sermonId}")
    public ResponseEntity<String> publishSermon(@PathVariable Integer sermonId) {
        /* Make the sermon media published. */
        boolean status = sermonMediaService.publishSermonMedia(sermonId);
        if (! status) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Published sermon media with id " + sermonId + " successfully");
    }

    @PutMapping("/private/{sermonId}")
    public ResponseEntity<String> privateSermon(@PathVariable Integer sermonId) {
        /* Make the sermon media private if published. */
        boolean status = sermonMediaService.privateSermonMedia(sermonId);
        if (! status) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Privated sermon media with id " + sermonId + " successfully");
    }
}
