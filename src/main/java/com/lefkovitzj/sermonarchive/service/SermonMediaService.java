package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.repository.SermonMediaRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class SermonMediaService {
    @Autowired
    private S3Service s3Service;
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
                              S3Client s3Client) {
        this.s3Client = s3Client;
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

    @Transactional
    public boolean addSermonMedia(SermonMedia sermonMedia, MultipartFile file) {
        try {
            String uploadedUrl = s3Service.uploadFile(file);
            // Update the sermonMedia object to reflect the uploaded location.
            sermonMedia.setResourceUrl(uploadedUrl);
            sermonMedia.setS3Key(file.getOriginalFilename());
            sermonMediaRepository.save(sermonMedia);
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Transactional
    public void updateSermonMedia(SermonMedia updatedSermonMedia) {
        sermonMediaRepository.save(updatedSermonMedia);
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
        return s3Service.downloadFile(sermonMedia.getS3Key());
    }

    @Transactional
    public boolean publishSermonMedia(Integer sermonId) {
        SermonMedia sermonMedia = getSermonMediaById(sermonId, true);
        // Check that the sermon was accessible (exists, whether published or not).
        if (sermonMedia == null) {
            return false;
        }

        // Publish the media (even if already published).
        sermonMedia.setPublished(true);
        return true;
    }

    @Transactional
    public boolean privateSermonMedia(Integer sermonId) {
        SermonMedia sermonMedia = getSermonMediaById(sermonId);
        // Check that the sermon was accessible (exists and published).
        if (sermonMedia == null) {
            // Sermon media does not exist or is not published.
            return false;
        }

        // The sermon media exists and is published.
        sermonMedia.setPublished(false);
        return true;
    }
}
