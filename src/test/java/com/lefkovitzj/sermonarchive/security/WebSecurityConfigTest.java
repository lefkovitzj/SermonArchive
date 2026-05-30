package com.lefkovitzj.sermonarchive.security;
import com.lefkovitzj.sermonarchive.entity.User;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lefkovitzj.sermonarchive.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WebSecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private static ApplicationContext applicationContext;

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Test
    void shouldAllowAnonymousAccessToAuthEndpoints() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowAnonymousAccessToSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAnonymousAccessToSecuredEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @BeforeEach
    void setUp() {
        String testUsername = "testuser";
        if (userService.getByName(testUsername) == null) {
            User testUser = new User();
            testUser.setUsername(testUsername);
            testUser.setPassword(passwordEncoder.encode("testpassword"));
            userService.addUser(testUser);
        }
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldAllowAuthenticatedAccessToSecuredEndpoints() throws Exception {
        String token = jwtUtil.generateToken("testuser");
        assertThat(jwtUtil.getUserFromToken(token)).isEqualTo("testuser");
        mockMvc.perform(get("/api/v1/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}