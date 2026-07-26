package com.lefkovitzj.sermonarchive.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser1;
    private User testUser2;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1);
        testUser1.setUsername("johndoe");
        testUser1.setEmail("john@example.com");

        testUser2 = new User();
        testUser2.setId(2);
        testUser2.setUsername("janedoe");
        testUser2.setEmail("jane@example.com");

        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testadmin")
                .password("password")
                .roles("USER")
                .build();
    }

    // ==========================================
    // GET /api/v1/auth
    // ==========================================

    @Test
    void getUsersShouldReturnListOfAllUsers() throws Exception {
        when(userService.listAllUsers()).thenReturn(List.of(testUser1, testUser2));

        mockMvc.perform(get("/api/v1/auth")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[1].username").value("janedoe"))
                .andExpect(jsonPath("$[1].email").value("jane@example.com"));

        verify(userService).listAllUsers();
    }

    // ==========================================
    // GET /api/v1/auth/search?username={username}
    // ==========================================

    @Test
    void getUsersByUsernameShouldReturnMatchingUsers() throws Exception {
        when(userService.searchUserByName("johndoe")).thenReturn(List.of(testUser1));

        mockMvc.perform(get("/api/v1/auth/search")
                        .param("username", "johndoe")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("johndoe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(userService).searchUserByName("johndoe");
    }

    // ==========================================
    // GET /api/v1/auth/user?id={id}
    // ==========================================

    @Test
    void getUserByIdShouldReturnUser() throws Exception {
        when(userService.searchUserById(1)).thenReturn(testUser1);

        mockMvc.perform(get("/api/v1/auth/user")
                        .param("id", "1")
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).searchUserById(1);
    }

    // ==========================================
    // POST /api/v1/auth/add
    // ==========================================

    @Test
    void addUserShouldCreateUserAndReturnSuccessMessage() throws Exception {
        doNothing().when(userService).addUser(any(User.class));

        mockMvc.perform(post("/api/v1/auth/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser1))
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("User johndoe (john@example.com) was added successfully."));

        // Use ArgumentCaptor to verify the payload received by the service
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).addUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("johndoe", capturedUser.getUsername());
        assertEquals("john@example.com", capturedUser.getEmail());
    }

    // ==========================================
    // POST /api/v1/auth/update
    // ==========================================

    @Test
    void updateUserShouldModifyUserAndReturnSuccessMessage() throws Exception {
        doNothing().when(userService).updateUser(any(User.class));

        testUser1.setUsername("john_updated");

        mockMvc.perform(post("/api/v1/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser1))
                        .with(user(mockUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("User john_updated was updated successfully."));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("john_updated", capturedUser.getUsername());
    }
}