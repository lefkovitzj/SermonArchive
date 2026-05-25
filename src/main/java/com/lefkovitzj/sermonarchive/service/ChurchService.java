package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.ChurchRepository;
import com.lefkovitzj.sermonarchive.repository.SpeakerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service public class ChurchService {
    private static final Logger logger = LoggerFactory.getLogger(ChurchService.class);

    @Autowired
    private ChurchRepository  churchRepository;
    private UserService userService;

    public ChurchService(UserService userService) {
        this.userService = userService;
    }


    public List<Church> getChurches() {
        return churchRepository.findAll();
    }
    public Church getChurchByName(String churchName) {
        return churchRepository.findByName(churchName);
    }

    @Transactional
    public void createNewChurch(String newName, User currentUser) {
        logger.info("User '{}' creating new church '{}'", currentUser.getUsername(), newName);
        Church newChurch = new Church();
        newChurch.name = newName;
        newChurch.owner = currentUser;
        churchRepository.save(newChurch);
        logger.info("New church '{}' created successfully", newName);
    }

    public boolean churchExists(String churchName) {
        Church church = getChurchByName(churchName);
        return church != null;
    }

    public boolean verifyOwnership(String churchName, UserDetails userDetails) {
        User currentUser =  userService.getByName(userDetails.getUsername());
        Church church = getChurchByName(churchName);
        if (church == null || currentUser == null) {
            return false;
        }
        return Objects.equals(church.owner.getId(), currentUser.getId());
    }

}
