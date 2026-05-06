package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.repository.SermonMediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;


@Service
public class SermonMediaService {
    private final S3Client s3Client;
    private final String bucketName;
    private final String publicUrlPattern;
    private final SermonMediaRepository sermonMediaRepository;

    public SermonMediaService(SermonMediaRepository sermonMediaRepository,
                              S3Client s3Client,
                              @Value("${s3.public-url-pattern}") String publicUrlPattern,
                              @Value("${s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.publicUrlPattern = publicUrlPattern;
        this.sermonMediaRepository = sermonMediaRepository;
    }

    public String getUploadedObjectUrl(String key) {
        return publicUrlPattern.replace("{bucket}", bucketName).replace("{key}", key);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return getUploadedObjectUrl(file.getOriginalFilename());
    }

    public List<SermonMedia> getSermonMedia() {
        return sermonMediaRepository.findAll();
    }

    @Transactional
    public boolean addSermonMedia(SermonMedia sermonMedia, MultipartFile file) {
        try {
            String uploadedUrl = uploadFile(file);
            // Update the sermonMedia object to reflect the uploaded location.
            sermonMedia.setResourceUrl(uploadedUrl);
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
}
