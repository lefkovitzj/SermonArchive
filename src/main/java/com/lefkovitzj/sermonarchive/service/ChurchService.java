package com.lefkovitzj.sermonarchive.service;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.Speaker;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.repository.ChurchRepository;
import com.lefkovitzj.sermonarchive.repository.SpeakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service public class ChurchService {
    @Autowired
    private ChurchRepository  churchRepository;

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

}
