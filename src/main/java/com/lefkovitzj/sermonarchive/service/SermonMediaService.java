package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.repository.SermonMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.List;


@Service
public class SermonMediaService {
    @Autowired
    private S3Service s3Service;
    private final S3Client s3Client;
    private final SermonMediaRepository sermonMediaRepository;

    public SermonMediaService(SermonMediaRepository sermonMediaRepository,
                              S3Client s3Client) {
        this.s3Client = s3Client;
        this.sermonMediaRepository = sermonMediaRepository;
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

    public SermonMedia getSermonMediaById(Integer sermonId) {
        SermonMedia sermonMedia = sermonMediaRepository.findById(sermonId).orElse(null);

        // Ensure the existence and publication of the requested sermon.
        if (sermonMedia == null || (sermonMedia != null && ! sermonMedia.isPublished())) {
            return null;
        }
        return sermonMedia;
    }

    public byte[] getFileForDownload(SermonMedia sermonMedia) {
        // Use the S3Service to get the bytes for the download target file.
        byte[] s3FileData = s3Service.downloadFile(sermonMedia.getS3Key());

        // Return the download target file bytes as an attachment.
        return s3FileData;
    }
}
