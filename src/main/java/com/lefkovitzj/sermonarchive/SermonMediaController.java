package com.lefkovitzj.sermonarchive;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/sermon-media")
public class SermonMediaController {
    @GetMapping
    public List<SermonMedia> getSermonMedia() {
        return List.of(
                new SermonMedia(
                        1,
                        "Test sermon 1",
                        "Testing the new SermonMedia model from a REST API endpoint",
                        "https://lefkovitzj.com",
                        "joseph",
                        List.of("test", "testing", "123"),
                        false,
                        false
                ),
                new SermonMedia(
                        2,
                        "Test sermon 2",
                        "Once again testing the new SermonMedia model from a REST API endpoint",
                        "https://lefkovitzj.com/projects",
                        "joseph l",
                        List.of("another-test", "testing", "123"),
                        true,
                        true
                )
        );
    }
}
