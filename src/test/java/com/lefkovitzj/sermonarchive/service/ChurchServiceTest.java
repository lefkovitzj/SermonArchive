package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.ChurchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChurchServiceTest {
    @Mock
    private ChurchRepository churchRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ChurchService churchService;

    @Test
    void verifyOwnershipReturnFalseWhenChurchDNE() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(churchRepository.findByName("dne")).thenReturn(null);

        boolean result = churchService.verifyOwnership("dne", userDetails);

        assertFalse(result);

    }

    @Test
    void verifyOwnershipReturnFalseWhenUserIdIsNotOwner() {
        when(userDetails.getUsername()).thenReturn("testuser");
        when(churchRepository.findByName("ownedByAnotherUser")).thenReturn(new Church(1, "ownedByAnotherUser", List.of(), new User(1, "nottestuser", "password", null, "")));

        boolean result = churchService.verifyOwnership("ownedByAnotherUser", userDetails);

        assertFalse(result);

    }

    @Test
    void verifyOwnershipReturnTrueWhenUserIdIsOwner() {
        User testUser = new User(1, "testuser", "password", null, "");
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.getByName("testuser")).thenReturn(testUser);
        when(churchRepository.findByName("ownedByTestUser")).thenReturn(new Church(1, "ownedByTestUser", List.of(), testUser));

        boolean result = churchService.verifyOwnership("ownedByTestUser", userDetails);

        assertTrue(result);
    }

    @Test
    void  churchReturnsTrueWhenChurchFound() {
        User testUser = new User(1, "testuser", "password", null, "");
        when(churchRepository.findByName("testChurch")).thenReturn(new Church(1, "testChurch", List.of(), testUser));

        boolean result = churchService.churchExists("testChurch");

        assertTrue(result);
    }
    @Test
    void churchReturnsFalseWhenChurchNotFound() {
        boolean result = churchService.churchExists("testchurch");

        assertFalse(result);
    }
}
