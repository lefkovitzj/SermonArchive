package com.lefkovitzj.sermonarchive.controller;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.service.ChurchService;
import com.lefkovitzj.sermonarchive.service.SermonMediaService;
import com.lefkovitzj.sermonarchive.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/sermon-media")
public class SermonMediaController {

    private final Logger logger = LoggerFactory.getLogger(SermonMediaController.class);
    private final SermonMediaService sermonMediaService;
    private final ChurchService churchService;

    public SermonMediaController(SermonMediaService sermonMediaService,
                                 ChurchService churchService) {
        this.sermonMediaService = sermonMediaService;
        this.churchService = churchService;
    }

    /* Upload, download, and stream sermon media. */
    @PostMapping(value = "/add")
    public ResponseEntity<String> addSermonMedia(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute SermonMedia newSermonMedia,
            @RequestParam("sermonFile") MultipartFile sermonFile,
            @RequestParam("churchName") String churchName) {
        if (! churchService.churchExists(churchName)) {
            return ResponseEntity.badRequest().body("Media cannot be added to non-existent church '" + churchName + "'");
        }
        if (! churchService.verifyOwnership(churchName, userDetails)) {
            return ResponseEntity.badRequest().body("User '" + userDetails.getUsername() + "' is not authorized to add media for the church '" + churchName + "'");
        }
        if (!sermonMediaService.isMedia(sermonFile)) {
            return ResponseEntity.badRequest().body("Invalid sermon media file type (" + sermonMediaService.getExt(sermonFile) + ")");
        }

        // Upload the file.
        sermonMediaService.addSermonMedia(newSermonMedia, sermonFile);
        return ResponseEntity.ok("Sermon media '" + newSermonMedia.getTitle() + "' of type " + (newSermonMedia.isVideo() ? "video" : "audio") +  " by " + newSermonMedia.getSpeaker() + " was added successfully. Uploaded to " + newSermonMedia.getResourceUrl() + ".");
    }

    @GetMapping("/{sermonId}/download")
    public ResponseEntity<InputStreamResource> downloadSermon(@PathVariable Integer sermonId) {
        SermonMedia sermonMedia = sermonMediaService.getSermonMediaById(sermonId);
        // Check that the sermon was accessible (exists and published).
        if (sermonMedia == null) {
            logger.warn("Download requested for missing or unpublished sermon ID [{}]", sermonId);
            return ResponseEntity.notFound().build();
        }
        // Get the bytes and format them for the response.
        try {

            InputStream mediaStream = sermonMediaService.getFileForDownload(sermonMedia);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sermonMedia.getTitle() + "\"")
                    .body(new InputStreamResource(mediaStream));
        }
        catch (Exception e) {
            logger.error("Failed to download file for sermon ID [{}] (Title: '{}')",
                    sermonId, sermonMedia.getTitle(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{sermonId}/embed")
    public ResponseEntity<InputStreamResource> embedSermon(@PathVariable Integer sermonId) {
        SermonMedia sermonMedia = sermonMediaService.getSermonMediaById(sermonId);
        // Check that the sermon was accessible (exists and published).
        if (sermonMedia == null) {
            logger.warn("Embed requested for missing or unpublished sermon ID [{}]", sermonId);
            return ResponseEntity.notFound().build();
        }
        // Get the bytes and format them for the response.
        try {
            InputStream mediaStream = sermonMediaService.getFileForDownload(sermonMedia);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + sermonMedia.getTitle() + "\"")
                    .body(new InputStreamResource(mediaStream));
        }
        catch (Exception e) {
            // The S3 interaction (most likely) or other logic failed.
            logger.error("Failed to stream file for sermon ID [{}] (Title: '{}')",
                    sermonId, sermonMedia.getTitle(), e);

            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{sermonId}/publish")
    public ResponseEntity<String> publishSermon(@PathVariable Integer sermonId) {
        /* Make the sermon media published. */
        boolean status = sermonMediaService.publishSermonMedia(sermonId);
        if (! status) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Published sermon media with id " + sermonId + " successfully");
    }

    @PutMapping("/{sermonId}/private")
    public ResponseEntity<String> privateSermon(@PathVariable Integer sermonId) {
        /* Make the sermon media private if published. */
        boolean status = sermonMediaService.privateSermonMedia(sermonId);
        if (! status) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Privated sermon media with id " + sermonId + " successfully");
    }

    /* Tag the sermon media. */
    @PostMapping("/{sermonId}/tags")
    public ResponseEntity<String> setSermonTags(@PathVariable Integer sermonId, @RequestBody List<String> tags) {
        boolean status = sermonMediaService.updateSermonMediaTags(sermonId, tags);
        if (! status) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Updated tags " + tags.toString() + " for sermon media with id " + sermonId + " successfully");
    }

    @PutMapping("/{sermonId}/tags/{tag}")
    public ResponseEntity<String> tagSermon(@PathVariable Integer sermonId, @PathVariable String tag) {
        boolean status = sermonMediaService.addSermonMediaTag(sermonId, tag);
        if (! status) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Added tag " + tag + " to sermon media with id " + sermonId + " successfully");
    }

    @DeleteMapping("/{sermonId}/tags/{tag}")
    public ResponseEntity<String> deleteSermonTag(@PathVariable Integer sermonId, @PathVariable String tag) {
        boolean status = sermonMediaService.removeSermonMediaTag(sermonId, tag);
        if (! status) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Deleted tag " + tag + " from sermon media with id " + sermonId + " successfully");
    }

    /* Query the sermon media. */
    @GetMapping()
    public ResponseEntity<List<SermonMedia>> getSermonMedia(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String speaker,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        // Filter by Tag
        if (tag != null && !tag.trim().isEmpty()) {
            return ResponseEntity.ok(sermonMediaService.getSermonMediaByTag(tag));
        }

        // Filter by Speaker
        if (speaker != null && !speaker.trim().isEmpty()) {
            return ResponseEntity.ok(sermonMediaService.getSermonMediaBySpeaker(speaker));
        }

        // Filter by Date Range
        if (start != null && end != null) {
            return ResponseEntity.ok(sermonMediaService.getSermonMediaBetweenTimes(start, end));
        }

        // Return all sermons if no query parameters are provided
        return ResponseEntity.ok(sermonMediaService.getSermonMedia());
    }
}
