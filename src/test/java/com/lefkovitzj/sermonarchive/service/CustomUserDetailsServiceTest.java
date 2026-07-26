package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenExists() {
        User testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void loadUserByUsernameShouldThrowExceptionWhenNotExists() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}
