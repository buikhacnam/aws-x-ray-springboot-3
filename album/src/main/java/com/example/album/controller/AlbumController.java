package com.example.album.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/albums")
@XRayEnabled
public class AlbumController {
    
    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    @GetMapping("/photos")
    public ResponseEntity<String> getPhotos(@RequestParam (value = "errorType", required = false) String errorType) {
        logger.info("Album service received request to get photos");
        if(errorType != null) {
            if (errorType.equals("400")) {
                logger.error("400 error in Album Service");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("album-service 400 error");
            } else if (errorType.equals("500")) {
                logger.error("500 error in Album Service");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("album-service 500 error");
            }
        }
        return ResponseEntity.ok("photos from album service");
    }
}
