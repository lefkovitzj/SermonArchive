package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.ChurchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ChurchServiceIntegrationTest {
    @Autowired
    private ChurchService churchService;

    @Autowired
    private ChurchRepository churchRepository;

    @Autowired
    private UserService userService;

    @Test
    void createNewChurchPersistsCorrectly() {
        User owner = new User();
        owner.setUsername("testuser");
        owner.setPassword("password");
        userService.addUser(owner);

        String churchName = "testchurch";
        churchService.createNewChurch(churchName, owner);

        Church savedChurch = churchRepository.findByName(churchName);

        assertThat(savedChurch).isNotNull();
        assertThat(savedChurch.getName()).isEqualTo(churchName);
        assertThat(savedChurch.getOwner().getUsername()).isEqualTo("testuser");
    }
}
