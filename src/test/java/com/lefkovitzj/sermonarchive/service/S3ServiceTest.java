package com.lefkovitzj.sermonarchive.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    private static final String TEST_BUCKET = "test-sermon-bucket";

    @BeforeEach
    void setUp() {
        // Inject the @Value("${s3.bucket-name}") field into s3Service
        ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET);
    }

    // ==========================================
    // uploadFile(...) Tests
    // ==========================================

    @Test
    void uploadFileShouldSuccessfullyUploadAndReturnS3Key() throws IOException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sermon.mp3",
                "audio/mpeg",
                "dummy audio content".getBytes()
        );

        String sermonName = "Sunday Morning Sermon";
        String fileExt = "mp3";

        String returnedKey = s3Service.uploadFile(mockFile, sermonName, fileExt);

        // Verify the key structure: sermons/<uuid>.mp3
        assertNotNull(returnedKey);
        assertTrue(returnedKey.startsWith("sermons/"));
        assertTrue(returnedKey.endsWith(".mp3"));

        // Verify S3Client putObject was called with correct bucket & key
        ArgumentCaptor<PutObjectRequest> putRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(putRequestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = putRequestCaptor.getValue();
        assertEquals(TEST_BUCKET, capturedRequest.bucket());
        assertEquals(returnedKey, capturedRequest.key());
    }

    @Test
    void uploadFileShouldThrowRuntimeExceptionWhenS3ClientFails() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sermon.mp3",
                "audio/mpeg",
                "dummy audio content".getBytes()
        );

        // Simulate AWS SDK Exception
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(SdkException.create("S3 connection error", new RuntimeException()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                s3Service.uploadFile(mockFile, "Faith & Perseverance", "mp3")
        );

        assertTrue(exception.getMessage().contains("S3 upload failed for sermon Faith & Perseverance"));
    }

    @Test
    void uploadFileShouldPropagateIOExceptionWhenInputStreamFails() throws IOException {
        MultipartFile faultyFile = mock(MultipartFile.class);
        when(faultyFile.getOriginalFilename()).thenReturn("sermon.mp3");
        when(faultyFile.getSize()).thenReturn(100L);
        when(faultyFile.getInputStream()).thenThrow(new IOException("Disk read error"));

        assertThrows(IOException.class, () ->
                s3Service.uploadFile(faultyFile, "Corrupted Sermon", "mp3")
        );
    }

    // ==========================================
    // downloadFile(...) Tests
    // ==========================================

    @Test
    void downloadFileShouldReturnInputStreamFromS3() {
        String s3Key = "sermons/12345-abc.mp3";
        Integer sermonId = 42;
        InputStream expectedStream = new ByteArrayInputStream("audio data".getBytes());

        when(s3Client.getObject(any(GetObjectRequest.class), any(ResponseTransformer.class)))
                .thenReturn(expectedStream);

        InputStream resultStream = s3Service.downloadFile(s3Key, sermonId);

        assertNotNull(resultStream);
        assertEquals(expectedStream, resultStream);

        // Verify GetObjectRequest arguments passed to S3Client
        ArgumentCaptor<GetObjectRequest> getRequestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(s3Client).getObject(getRequestCaptor.capture(), any(ResponseTransformer.class));

        GetObjectRequest capturedRequest = getRequestCaptor.getValue();
        assertEquals(TEST_BUCKET, capturedRequest.bucket());
        assertEquals(s3Key, capturedRequest.key());
    }

    @Test
    void downloadFileShouldThrowRuntimeExceptionWhenS3ClientFails() {
        String s3Key = "sermons/non-existent.mp3";
        Integer sermonId = 99;

        when(s3Client.getObject(any(GetObjectRequest.class), any(ResponseTransformer.class)))
                .thenThrow(SdkException.create("Object not found", new RuntimeException()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                s3Service.downloadFile(s3Key, sermonId)
        );

        assertTrue(exception.getMessage().contains("S3 download failed for sermon 99"));
    }
}