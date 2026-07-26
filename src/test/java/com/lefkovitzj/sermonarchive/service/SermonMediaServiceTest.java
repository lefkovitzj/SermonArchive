package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.repository.SermonMediaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SermonMediaServiceTest {
    @Mock
    private S3Service s3Service;

    @Mock
    private SpeakerService speakerService;

    @Mock
    private SermonMediaRepository sermonMediaRepository;

    @InjectMocks
    private SermonMediaService sermonMediaService;

    @Mock
    private MultipartFile file;

    @Test
    void verifyGetExtReturnsValidExtension() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp3");

        String ext = sermonMediaService.getExt(file);

        assertEquals("mp3", ext);
    }

    @Test
    void verifyGetExtReturnsInvalidIfNoExtension() {
        when(file.getOriginalFilename()).thenReturn("README");

        String ext = sermonMediaService.getExt(file);

        assertEquals("", ext);
    }

    @Test
    void verifyGetExtReturnsLastExtensionIfMultiple() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp3.exe");

        String ext = sermonMediaService.getExt(file);

        assertEquals("exe", ext);
    }

    @Test
    void verifyIsVideoReturnsTrueForVideo() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp4");
        when(file.getContentType()).thenReturn("video/mp4");

        boolean result = sermonMediaService.isVideo(file);

        assertTrue(result);
    }

    @Test
    void verifyIsVideoReturnsFalseForNonVideo() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp3");

        boolean result = sermonMediaService.isVideo(file);

        assertFalse(result);
    }

    @Test
    void verifyIsVideoReturnsFalseForVideoWithMismatch() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp4");
        when(file.getContentType()).thenReturn("audio/mp3");

        boolean result = sermonMediaService.isVideo(file);

        assertFalse(result);
    }

    @Test
    void verifyIsAudioReturnsTrueForAudio() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp3");
        when(file.getContentType()).thenReturn("audio/mp3");

        boolean result = sermonMediaService.isAudio(file);

        assertTrue(result);
    }

    @Test
    void verifyIsAudioReturnsFalseForNonAudio() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp4");

        boolean result = sermonMediaService.isAudio(file);

        assertFalse(result);
    }

    @Test
    void verifyIsAudioReturnsFalseForAudioWithMismatch() {
        when(file.getOriginalFilename()).thenReturn("sermon.mp3");
        when(file.getContentType()).thenReturn("video/mp4");

        boolean result = sermonMediaService.isAudio(file);

        assertFalse(result);
    }

    // TODO: Add Integration tests for addSermonMedia()

    @Test
    void getSermonMediaByIdShouldReturnSermonMediaWhenExistsAndPublished() {
        SermonMedia testSermonMedia = new SermonMedia();
        testSermonMedia.setPublished(true);
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testSermonMedia));

        SermonMedia result = sermonMediaService.getSermonMediaById(1);

        assertEquals(testSermonMedia, result);
    }

    @Test
    void getSermonMediaByIdShouldReturnNullWhenNotFound() {
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.empty());

        SermonMedia result = sermonMediaService.getSermonMediaById(1);

        assertNull(result);
    }

    @Test
    void getSermonMediaByIdShouldReturnNullWhenExistsButNotPublished(){
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(new SermonMedia()));

        SermonMedia result = sermonMediaService.getSermonMediaById(1);

        assertNull(result);
    }

    @Test
    void getSermonMediaIncludeUnpublishedShouldReturnNullWhenExistsButNotPublished(){
        SermonMedia testSermonMedia = new SermonMedia();
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testSermonMedia));

        SermonMedia result = sermonMediaService.getSermonMediaById(1, true);

        assertEquals(testSermonMedia, result);
    }

    @Test
    void addSermonMediaShouldReturnTrueWhenVideoUploaded() throws IOException {
        when(file.getOriginalFilename()).thenReturn("sermon.mp4");
        when(file.getContentType()).thenReturn("video/mp4");

        SermonMedia testMedia = new SermonMedia();
        when(s3Service.uploadFile(any(), any(), any())).thenReturn("s3/key.mp4");

        boolean result = sermonMediaService.addSermonMedia(testMedia, file);

        assertTrue(result);
        assertTrue(testMedia.isVideo());
        verify(sermonMediaRepository).save(any(SermonMedia.class));
    }

    @Test
    void addSermonMediaShouldReturnTrueWhenAudioUploaded() throws IOException {
        when(file.getOriginalFilename()).thenReturn("sermon.mp3");
        when(file.getContentType()).thenReturn("audio/mp3");

        SermonMedia testMedia = new SermonMedia();
        when(s3Service.uploadFile(any(), any(), any())).thenReturn("s3/key.mp3");

        boolean result = sermonMediaService.addSermonMedia(testMedia, file);

        assertTrue(result);
        assertFalse(testMedia.isVideo());
        verify(sermonMediaRepository).save(any(SermonMedia.class));
    }

    @Test
    void addSermonMediaShouldReturnFalseWhenInvalidTypeUploaded() throws IOException {
        when(file.getOriginalFilename()).thenReturn("nonSermon.pdf");

        boolean result = sermonMediaService.addSermonMedia(new SermonMedia(), file);

        assertFalse(result);
        verifyNoInteractions(s3Service);
        verifyNoInteractions(sermonMediaRepository);
    }

    @Test
    void addSermonMediaShouldReturnFalseWhenS3UploadFails() throws IOException {
        when(file.getOriginalFilename()).thenReturn("sermon.mp4");
        when(file.getContentType()).thenReturn("video/mp4");

        when(s3Service.uploadFile(any(), any(), any())).thenThrow(new RuntimeException("S3 upload failed"));

        boolean result = sermonMediaService.addSermonMedia(new SermonMedia(), file);

        assertFalse(result);
        verify(sermonMediaRepository, never()).save(any());
    }

    @Test
    void publishSermonMediaShouldReturnTrueWhenMediaExists() {
        SermonMedia testMedia = new SermonMedia();
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.publishSermonMedia(1);

        assertTrue(result);
    }

    @Test
    void publishSermonMediaShouldReturnFalseWhenMediaDoesNotExist() {
        boolean result = sermonMediaService.publishSermonMedia(-1);

        assertFalse(result);
    }

    @Test
    void privateSermonMediaShouldReturnTrueWhenMediaExists() {
        SermonMedia testMedia = new SermonMedia();
        testMedia.setPublished(true);
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.privateSermonMedia(1);

        assertTrue(result);
    }

    @Test
    void privateSermonMediaShouldReturnFalseWhenMediaExistsButNotPublished() {
        SermonMedia testMedia = new SermonMedia();
        testMedia.setPublished(false);
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.privateSermonMedia(1);

        assertFalse(result);
    }

    @Test
    void privateSermonMediaShouldReturnFalseWhenMediaDoesNotExist() {
        boolean result = sermonMediaService.privateSermonMedia(-1);

        assertFalse(result);
    }

    @Test
    void addSermonMediaTagShouldReturnTrueWhenMediaExistsAndTagNew() {
        SermonMedia testMedia = new SermonMedia();
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.addSermonMediaTag(1, "sermon");

        assertTrue(result);
    }

    @Test
    void addSermonMediaTagShouldReturnTrueWhenMediaExistsAndTagNotNew() {
        SermonMedia testMedia = new SermonMedia();
        testMedia.appendTag("sermon");
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.addSermonMediaTag(1, "sermon");

        assertTrue(result);
    }

    @Test
    void addSermonMediaTagShouldReturnFalseWhenMediaDoesNotExist() {
        boolean result = sermonMediaService.addSermonMediaTag(-1, "sermon");

        assertFalse(result);
    }

    @Test
    void removeSermonMediaTagShouldReturnTrueWhenMediaExistsAndTagNew() {
        SermonMedia testMedia = new SermonMedia();
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.removeSermonMediaTag(1, "sermon");

        assertTrue(result);
    }

    @Test
    void removeSermonMediaTagShouldReturnTrueWhenMediaExistsAndTagNotNew() {
        SermonMedia testMedia = new SermonMedia();
        testMedia.appendTag("sermon");
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.removeSermonMediaTag(1, "sermon");

        assertTrue(result);
    }


    @Test
    void removeSermonMediaTagShouldReturnFalseWhenMediaDoesNotExist() {
        boolean result = sermonMediaService.removeSermonMediaTag(-1, "sermon");

        assertFalse(result);
    }

    @Test
    void updateSermonMediaTagsShouldReturnTrueWhenMediaExists() {
        SermonMedia testMedia = new SermonMedia();
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.updateSermonMediaTags(1, List.of("test", "sermon"));

        assertTrue(result);
    }

    @Test
    void updateSermonMediaTagsShouldReturnFalseWhenMediaDoesNotExist() {
        boolean result = sermonMediaService.updateSermonMediaTags(-1, List.of("test", "sermon"));

        assertFalse(result);
    }

    @Test
    void setSermonMediaTimeShouldReturnTrueWhenMediaExists() {
        SermonMedia testMedia = new SermonMedia();
        when(sermonMediaRepository.findById(1)).thenReturn(Optional.of(testMedia));

        boolean result = sermonMediaService.setSermonMediaTime(1, LocalDateTime.now());

        assertTrue(result);
    }

    @Test
    void setSermonMediaTimeShouldReturnFalseWhenMediaDoesNotExist() {
        boolean result = sermonMediaService.setSermonMediaTime(-1, LocalDateTime.now());

        assertFalse(result);
    }

}
