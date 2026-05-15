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
@RequestMapping("api/v1/sermon-media/video")
public class SermonVideoController {

    private final SermonMediaService sermonMediaService;

    public SermonVideoController(SermonMediaService sermonMediaService) {
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
            return ResponseEntity.badRequest().body("Invalid sermon media file type.");
        }
        if (!sermonMediaService.isVideo(sermonFile)) {
            return ResponseEntity.badRequest().body("Invalid sermon media file type - must be video not audio.");
        }
        newSermonMedia.setVideo(true);
        sermonMediaService.addSermonMedia(newSermonMedia, sermonFile);
        return ResponseEntity.ok("Sermon video " + newSermonMedia.getTitle() + " by " + newSermonMedia.getSpeaker() + " was added successfully. Uploaded to " + newSermonMedia.getResourceUrl() + ".");
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
}
