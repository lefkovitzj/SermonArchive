package com.lefkovitzj.sermonarchive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service public class S3Service {
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${s3.bucket-name}") String bucketName;
    @Autowired
    S3Client s3Client;

    public String uploadFile(MultipartFile file, Integer sermonId) throws IOException {
        // Create the object to write to s3 from the uploaded file.
        String uniqueKey  = "sermons/" + sermonId + "_" + file.getOriginalFilename();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .build();
        // Write the file and return its uploaded url.
        logger.info("Starting file upload to S3 for sermon {} with file [{}] (size: {} bytes)", sermonId,  file.getOriginalFilename(), file.getSize());
        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        }
        catch (SdkException e) {
            logger.error("S3 upload failed for sermon {}", sermonId, e);
            throw new RuntimeException("S3 upload failed for sermon " + sermonId, e);
        }

        return uniqueKey;
    }

    public InputStream downloadFile(String s3Key, Integer sermonId) {
        // Request the file from s3 and store it as a byte array.
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        // Return the data stream
        logger.info("Starting S3 stream download for sermon {} with key {}", sermonId, s3Key);
        try {
            InputStream returnStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
            return returnStream;
        }
        catch (SdkException e) {
            logger.error("S3 download failed for sermon {}", sermonId, e);
            throw new RuntimeException("S3 download failed for sermon " + sermonId, e);
        }
    }
}
