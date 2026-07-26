package com.lefkovitzj.sermonarchive.controller;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.service.ChurchService;
import com.lefkovitzj.sermonarchive.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChurchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChurchService churchService;

    @MockitoBean
    private UserService userService;

    private Church testChurch1;
    private Church testChurch2;
    private User testUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testadmin");
        testUser.setEmail("admin@example.com");

        testChurch1 = new Church();
        testChurch1.setName("test church 1");

        testChurch2 = new Church();
        testChurch2.setName("test church 2");

        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testadmin")
                .password("password")
                .roles("USER")
                .build();
    }

    // ==========================================
    // GET /api/v1/church/list
    // ==========================================

    @Test
    void listChurchesShouldReturnListOfChurches() throws Exception {
        when(churchService.getChurches()).thenReturn(List.of(testChurch1, testChurch2));

        mockMvc.perform(get("/api/v1/church/list")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("test church 1"))
                .andExpect(jsonPath("$[1].name").value("test church 2"));

        verify(churchService).getChurches();
    }

    // ==========================================
    // POST /api/v1/church/add?churchName={churchName}
    // ==========================================

    @Test
    void addChurchShouldCreateChurchAndReturnSuccessMessage() throws Exception {
        String newChurchName = "Calvary Chapel";

        when(userService.getByName("testadmin")).thenReturn(testUser);
        doNothing().when(churchService).createNewChurch(eq(newChurchName), any(User.class));

        mockMvc.perform(post("/api/v1/church/add")
                        .param("churchName", newChurchName)
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("New church 'Calvary Chapel' created successfully."));

        verify(userService).getByName("testadmin");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);

        verify(churchService).createNewChurch(nameCaptor.capture(), userCaptor.capture());

        assertEquals(newChurchName, nameCaptor.getValue());
        assertEquals("testadmin", userCaptor.getValue().getUsername());
    }
}