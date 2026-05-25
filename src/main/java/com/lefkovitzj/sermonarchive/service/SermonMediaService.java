package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.SermonMediaRepository;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class SermonMediaService {
    private static final Logger logger = LoggerFactory.getLogger(SermonMediaService.class);
    @Autowired
    private S3Service s3Service;
    private SpeakerService speakerService;
    private final S3Client s3Client;
    private final SermonMediaRepository sermonMediaRepository;

    public final List<String> videoFileExts = List.of(
            "mp4",
            "webm"
    );
    public final List<String> audioFileExts = List.of(
            "mp3"
    );

    public SermonMediaService(SermonMediaRepository sermonMediaRepository,
                              SpeakerService speakerService,
                              S3Client s3Client) {
        this.s3Client = s3Client;
        this.speakerService = speakerService;
        this.sermonMediaRepository = sermonMediaRepository;
    }

    public String getExt(@NonNull MultipartFile file) {
        /* Get the file extension. */
        return  file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
    }
    public boolean isVideo(MultipartFile file) {
        /* Check whether the file is an accepted video file. */
        return videoFileExts.contains(getExt(file));
    }
    public boolean isAudio(MultipartFile file) {
        /* Check whether the file is an accepted audio file. */
        return audioFileExts.contains(getExt(file));
    }
    public boolean isMedia(MultipartFile file) {
        /* Check whether the file is an accepted media (audio or video) file. */
        return isVideo(file) || isAudio(file);
    }

    public List<SermonMedia> getSermonMedia() {
        return sermonMediaRepository.findAll();
    }
    public List<SermonMedia> getSermonMediaByTag(String tag) {
        /* Get all sermon media with a given tag. */
        return sermonMediaRepository.findAll()
                .stream()
                .filter(
                        sermonMedia -> sermonMedia.containsTag(tag)
                ).toList();
    }

    public List<SermonMedia> getSermonMediaBySpeaker(String speaker) {
        /* Get all sermon media from a given speaker. */
        Speaker querySpeaker = speakerService.getSpeakerByName(speaker);
        return sermonMediaRepository.findAll()
                .stream()
                .filter(
                        sermonMedia -> Objects.equals(sermonMedia.getSpeaker(), querySpeaker)
                ).toList();
    }
    public List<SermonMedia> getSermonMediaBetweenTimes(LocalDateTime  start, LocalDateTime end) {
        return sermonMediaRepository.findAll()
                .stream()
                .filter(
                        sermonMedia -> (start.isBefore(sermonMedia.getSermonDatetime()) && end.isAfter(sermonMedia.getSermonDatetime()))
                ).toList();
    }

    @Transactional
    public boolean addSermonMedia(SermonMedia sermonMedia, MultipartFile file) {
        /* Process the file upload and new sermon media object, adding them to the db and S3. */
        try {
            logger.info("Starting upload of new sermon from file upload '{}'", file.getOriginalFilename());
            // Handle file types.
            if (isVideo(file)) {
                // File is video.
                sermonMedia.setVideo(true);
            }
            else if (isAudio(file)) {
                // File is audio.
                sermonMedia.setVideo(false);
            }
            else {
                // File is neither video nor audio.
                logger.warn("Could not add sermon media {} from uploaded file - invalid file extension",  file.getOriginalFilename());
                return false;
            }
            // Save the new sermon media object to the db.
            sermonMediaRepository.save(sermonMedia);
            Integer sermonId = sermonMedia.getId();
            logger.info("Added sermon media {}", sermonId);

            // Upload the file to s3.
            String uniqueKey = s3Service.uploadFile(file, sermonId);
            sermonMedia.setS3Key(uniqueKey);
            logger.info("Uploaded sermon media {} to S3 {} as {}",  sermonId, (sermonMedia.isVideo() ? "video" : "audio"), uniqueKey);
            return true;
        }
        catch (Exception e) {
            logger.error("Could not add sermon media from uploaded file {}", file.getOriginalFilename(), e);
            return false;
        }
    }

    public SermonMedia getSermonMediaById(Integer sermonId, boolean includeUnpublished) {
        /* Get sermon media given its integer ID, with an optional arg to allow unpublished media */
        SermonMedia sermonMedia = sermonMediaRepository.findById(sermonId).orElse(null);

        if (! includeUnpublished) {
            // Do not include any unpublished sermon media.
            // Ensure the existence and publication of the requested sermon.
            if (sermonMedia != null && !sermonMedia.isPublished()) {
                return null;
            }
        }
        return sermonMedia;
    }

    public SermonMedia getSermonMediaById(Integer sermonId) {
        /* Overload the getSermonMediaById to default to no unpublished media if not specified. */
        return getSermonMediaById(sermonId, false);
    }

    public InputStream getFileForDownload(SermonMedia sermonMedia) {
        // Return the download target file bytes as an attachment.
        return s3Service.downloadFile(sermonMedia.getS3Key(), sermonMedia.getId());
    }

    @Transactional
    public boolean publishSermonMedia(Integer sermonId) {
        SermonMedia sermonMedia = getSermonMediaById(sermonId, true);
        // Check that the sermon was accessible (exists, whether published or not).
        if (sermonMedia == null) {
            logger.warn("Cannot publish sermon media {} - DNE",  sermonId);
            return false;
        }
        if (sermonMedia.isPublished()) {
            logger.info("Skipping publish sermon media {} - already published", sermonId);
        }

        // Publish the media (even if already published).
        sermonMedia.setPublished(true);
        logger.info("Published sermon media {}", sermonId);
        return true;
    }

    @Transactional
    public boolean privateSermonMedia(Integer sermonId) {
        SermonMedia sermonMedia = getSermonMediaById(sermonId);
        // Check that the sermon was accessible (exists and published).
        if (sermonMedia == null) {
            // Sermon media does not exist or is not published.
            logger.warn("Cannot private sermon media {} - DNE or private already",  sermonId);
            return false;
        }

        // The sermon media exists and is published.
        sermonMedia.setPublished(false);
        logger.info("Made private sermon media {}", sermonId);
        return true;
    }

    @Transactional
    public boolean addSermonMediaTag(Integer sermonId, String tag) {
        SermonMedia sermonMedia = getSermonMediaById(sermonId, true);
        // Check that the sermon was accessible (exists, whether published or not).
        if (sermonMedia == null) {
            logger.warn("Cannot add tag {} to sermon media {} - DNE or private", tag, sermonId);
            return false;
        }

        if (sermonMedia.getTags().contains(tag)) {
            logger.info("Skipping add tag {} to sermon media {} - already present",  tag, sermonId);
        }
        else {
            sermonMedia.appendTag(tag);
            logger.info("Added tags {} to sermon media {}", tag, sermonId);
        }
        return true;
    }

    @Transactional
    public boolean removeSermonMediaTag(Integer sermonId, String tag) {
        SermonMedia sermonMedia = getSermonMediaById(sermonId, true);
        // Check that the sermon was accessible (exists, whether published or not).
        if (sermonMedia == null) {
            logger.warn("Cannot remove tag {} from sermon media {} - DNE or private", tag, sermonId);
            return false;
        }

        if  (! sermonMedia.getTags().contains(tag)) {
            logger.info("Skipping remove tag {} from sermon media {} - not present",  tag, sermonId);
            return true;
        }
        else {
            sermonMedia.removeTag(tag);
            logger.info("Removed tags {} from sermon media {}", tag, sermonId);
        }
        return true;
    }

    @Transactional
    public boolean updateSermonMediaTags(Integer sermonId, List<String> tags) {
        SermonMedia sermonMedia = getSermonMediaById(sermonId, true);
        // Check that the sermon was accessible (exists, whether published or not).
        if (sermonMedia == null) {
            logger.warn("Cannot add tags {} to sermon media {} - DNE or private", tags.toString(), sermonId);
            return false;
        }

        sermonMedia.setTags(tags);
        logger.info("Adding tags {} to sermon media {}", tags.toString(), sermonId);
        return true;
    }
}
