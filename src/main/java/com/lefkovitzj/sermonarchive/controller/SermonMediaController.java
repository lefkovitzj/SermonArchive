package com.lefkovitzj.sermonarchive.controller;

import com.lefkovitzj.sermonarchive.service.SermonMediaService;
import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

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
    @GetMapping("/download/{sermonId}")
    public ResponseEntity<byte[]> downloadSermon(@PathVariable Integer sermonId) {
        SermonMedia sermonMedia = sermonMediaService.getSermonMediaById(sermonId);
        // Check that the sermon was accessible (exists and published).
        if (sermonMedia == null) {
            return ResponseEntity.notFound().build();
        }
        // Get the bytes and format them for the response.
        byte[] downloadableBytes = sermonMediaService.getFileForDownload(sermonMedia);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sermonMedia.getTitle() + "\"")
                .body(downloadableBytes);
    }
}
