package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.ChurchRepository;
import com.lefkovitzj.sermonarchive.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service public class ChurchService {
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
        Church newChurch = new Church();
        newChurch.name = newName;
        newChurch.owner = currentUser;
        churchRepository.save(newChurch);
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
        System.out.println(church.owner);
        System.out.println(currentUser);
        return church.owner == currentUser;
    }

}
