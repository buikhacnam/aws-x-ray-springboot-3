package com.example.user.service;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.example.user.client.TracedRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@XRayEnabled
public class AlbumService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);

    @Autowired
    private TracedRestClient restClient;

    @Value("${album.service.url:http://localhost:8081}")
    private String albumServiceUrl;

    public String getAlbums(String errorType, String errorAt) {
        logger.info("Calling album service to get albums with errorType: " + errorType + " and errorAt: " + errorAt);

        if ("user-service".equals(errorAt)) {
            if (errorType.equals("400")) {
                logger.error("400 error in User Service");
                throw new IllegalArgumentException("user-service 400 error");
            } else if (errorType.equals("500")) {
                logger.error("500 error in User Service");
                throw new RuntimeException("user-service 500 error");
            }
        }

        try {
            // Use the traced client which will add X-Ray headers automatically
            String result = restClient.getForObject(
                    albumServiceUrl + "/albums/photos" + "?errorType=" + errorType,
                    String.class,
                    "Call-Album-Service"
            );

            return result;
        } catch (Exception e) {
            // Log the error but rethrow the ResponseStatusException to preserve status code
            logger.error("ResponseStatusException from album service: {}", e.getMessage());
            throw e;
        }
    }
} 