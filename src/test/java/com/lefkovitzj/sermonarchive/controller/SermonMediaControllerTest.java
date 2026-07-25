package com.lefkovitzj.sermonarchive.controller;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.SermonMedia;
import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.service.ChurchService;
import com.lefkovitzj.sermonarchive.service.SermonMediaService;
import com.lefkovitzj.sermonarchive.service.SpeakerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SermonMediaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private SpeakerService speakerService;
    @MockitoBean private SermonMediaService sermonMediaService;
    @MockitoBean private ChurchService churchService;

    private UserDetails createMockUser(String username) {
        return User.withUsername(username)
                .password("password")
                .roles("USER")
                .build();
    }

    @Test
    void addSermonMediaShouldReturnErrorWhenNotChurchOwner() throws Exception {
        Speaker testSpeaker = new Speaker("Test Speaker");
        com.lefkovitzj.sermonarchive.entity.User testOwner = new com.lefkovitzj.sermonarchive.entity.User();
        testOwner.setUsername("testowner");

        when(churchService.getChurchByName(anyString())).thenReturn(new Church(1, "Test Church", List.of(testSpeaker), testOwner));
        when(churchService.churchExists(anyString())).thenReturn(true);
        when(churchService.verifyOwnership(anyString(), any())).thenReturn(false);

        UserDetails mockUserDetails = createMockUser("testuser");

        MockMultipartFile mockFile = new MockMultipartFile(
                "sermonFile",
                "test.mp4",
                "video/mp4",
                "invalid content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/sermon-media/add")
                        .file(mockFile)
                        .param("title", "Test Title")
                        .param("speaker", "Test Speaker")
                        .param("churchName", "Test Church")
                        .with(user(mockUserDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User 'testuser' is not authorized to add media for the church 'Test Church'"));
    }

    @Test
    void addSermonMediaShouldReturnErrorWhenChurchDoesNotExist() throws Exception {
        when(churchService.churchExists(anyString())).thenReturn(false);

        UserDetails mockUserDetails = createMockUser("testuser");

        MockMultipartFile mockFile = new MockMultipartFile(
                "sermonFile",
                "test.mp4",
                "video/mp4",
                "invalid content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/sermon-media/add")
                        .file(mockFile)
                        .param("title", "Test Title")
                        .param("speaker", "Test Speaker")
                        .param("churchName", "Nonexistent Church")
                        .with(user(mockUserDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Media cannot be added to non-existent church 'Nonexistent Church'"));
    }

    @Test
    void addSermonMediaShouldReturnErrorWhenInvalidFileType() throws Exception {
        when(churchService.churchExists(anyString())).thenReturn(true);
        when(churchService.verifyOwnership(anyString(), any())).thenReturn(true);
        when(sermonMediaService.isVideo(any())).thenReturn(false);
        when(sermonMediaService.isAudio(any())).thenReturn(false);
        when(sermonMediaService.getExt(any())).thenReturn("txt");

        UserDetails mockUserDetails = createMockUser("testuser");

        MockMultipartFile mockFile = new MockMultipartFile(
                "sermonFile",
                "document.txt",
                "text/plain",
                "some text".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/sermon-media/add")
                        .file(mockFile)
                        .param("title", "Test Title")
                        .param("speaker", "Test Speaker")
                        .param("churchName", "Test Church")
                        .with(user(mockUserDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid sermon media file type (txt)"));
    }

    @Test
    void addSermonMediaShouldReturnServerErrorWhenServiceFails() throws Exception {
        Speaker testSpeaker = new Speaker("Test Speaker");
        com.lefkovitzj.sermonarchive.entity.User testOwner = new com.lefkovitzj.sermonarchive.entity.User();
        testOwner.setUsername("testuser");

        when(churchService.churchExists(anyString())).thenReturn(true);
        when(churchService.verifyOwnership(anyString(), any())).thenReturn(true);
        when(churchService.getChurchByName(anyString())).thenReturn(new Church(1, "Test Church", List.of(testSpeaker), testOwner));
        when(sermonMediaService.isVideo(any())).thenReturn(true);
        when(sermonMediaService.addSermonMedia(any(SermonMedia.class), any())).thenReturn(false);

        UserDetails mockUserDetails = createMockUser("testuser");

        MockMultipartFile mockFile = new MockMultipartFile(
                "sermonFile",
                "test.mp4",
                "video/mp4",
                "video content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/sermon-media/add")
                        .file(mockFile)
                        .param("title", "Test Title")
                        .param("speaker", "Test Speaker")
                        .param("churchName", "Test Church")
                        .with(user(mockUserDetails)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to add sermon media 'Test Title'."));
    }

    @Test
    void addSermonMediaShouldReturnSuccessWhenValid() throws Exception {
        Speaker testSpeaker = new Speaker("Test Speaker");
        com.lefkovitzj.sermonarchive.entity.User testOwner = new com.lefkovitzj.sermonarchive.entity.User();
        testOwner.setUsername("testuser");

        when(churchService.churchExists(anyString())).thenReturn(true);
        when(churchService.verifyOwnership(anyString(), any())).thenReturn(true);
        when(churchService.getChurchByName(anyString())).thenReturn(new Church(1, "Test Church", List.of(testSpeaker), testOwner));
        when(sermonMediaService.isVideo(any())).thenReturn(true);

        // Mock success and populate S3 key if set inside the service
        when(sermonMediaService.addSermonMedia(any(SermonMedia.class), any())).thenAnswer(invocation -> {
            SermonMedia media = invocation.getArgument(0);
            media.setS3Key("sermons/test-key.mp4");
            media.setVideo(true);
            return true;
        });

        UserDetails mockUserDetails = createMockUser("testuser");

        MockMultipartFile mockFile = new MockMultipartFile(
                "sermonFile",
                "test.mp4",
                "video/mp4",
                "valid video content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/sermon-media/add")
                        .file(mockFile)
                        .param("title", "Test Title")
                        .param("speaker", "Test Speaker")
                        .param("churchName", "Test Church")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("Sermon media 'Test Title' of type video by Test Speaker was added successfully. Uploaded with key 'sermons/test-key.mp4'."));

        verify(sermonMediaService).addSermonMedia(any(SermonMedia.class), any());
    }
}