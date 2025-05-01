package com.example.user.controller;

import com.example.user.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AlbumService albumService;

    @GetMapping("/albums")
    public ResponseEntity<String> getAlbums(
            @RequestParam(value = "errorType", required = false) String errorType,
            @RequestParam(value = "errorAt", required = false) String errorAt

    ) {
        logger.info("Received request to get albums");
        String errAt = errorAt != null ? errorAt : "album-service";
        String result = albumService.getAlbums(errorType, errAt);
        return ResponseEntity.ok(result);
    }
}
