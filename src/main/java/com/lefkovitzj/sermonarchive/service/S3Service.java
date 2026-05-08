package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service public class S3Service {
    @Value("${s3.public-url-pattern}") String publicUrlPattern;
    @Value("${s3.bucket-name}") String bucketName;
    @Autowired
    S3Client s3Client;
    public String getUploadedObjectUrl(String bucketName, String key) {
        return publicUrlPattern.replace("{bucket}", bucketName).replace("{key}", key);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Create the object to write to s3 from the uploaded file.
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                .build();
        // Write the file and return its uploaded url.
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return getUploadedObjectUrl(bucketName, file.getOriginalFilename());
    }

    public InputStream downloadFile(String s3Key) {
        // Request the file from s3 and store it as a byte array.
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();
        // Return the data stream
        return s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
    }
}
