package com.lefkovitzj.sermonarchive.controller;

import com.lefkovitzj.sermonarchive.entity.Church;
import com.lefkovitzj.sermonarchive.entity.User;
import com.lefkovitzj.sermonarchive.service.ChurchService;
import com.lefkovitzj.sermonarchive.service.SpeakerService;
import com.lefkovitzj.sermonarchive.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/church")
public class ChurchController {
    private final ChurchService churchService;
    private final UserService userService;

    public ChurchController(ChurchService churchService, UserService userService) {
        this.churchService = churchService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Church>> listChurches() {
        return ResponseEntity.ok(churchService.getChurches());
    }

    @PostMapping("/add")
    public ResponseEntity<String> addChurch(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam String churchName) {
        User currentUser = userService.getByName(userDetails.getUsername());
        churchService.createNewChurch(churchName, currentUser);
        return ResponseEntity.ok("New church '" + churchName + "' created successfully.");
    }
}
