package com.example.user.client;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * A client service that wraps RestTemplate and adds X-Ray trace headers to requests.
 */
@Component
public class TracedRestClient {

    private static final Logger logger = LoggerFactory.getLogger(TracedRestClient.class);
    
    @Autowired
    private RestTemplate restTemplate;
    

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, Object body, 
                                        Class<T> responseType, String subsegmentName) {
        
        Subsegment subsegment = AWSXRay.beginSubsegment(subsegmentName);
        
        try {
            // Create headers with X-Ray trace info
            HttpHeaders headers = new HttpHeaders();
            
            // Add trace headers if X-Ray segment exists
            addTraceHeaders(headers);
            
            // Create HttpEntity with headers and body
            HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);
            
            // Make the request
            ResponseEntity<T> response = restTemplate.exchange(
                url, method, requestEntity, responseType);
            
            // Check for non-2XX status codes
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Non-2XX response from service: {} - Status: {}", url, response.getStatusCode());
                subsegment.putAnnotation("service-call", "error");
                subsegment.putAnnotation("status-code", response.getStatusCode().value());
                throw new ResponseStatusException(response.getStatusCode(), "Error response from service: " + url);
            }
            
            subsegment.putAnnotation("service-call", "success");
            return response;
            
        } catch (HttpStatusCodeException e) {
            // Catch RestTemplate exceptions for status codes
            logger.error("HTTP error calling service: {} - Status: {}", url, e.getStatusCode());
            subsegment.putAnnotation("service-call", "error");
            subsegment.putAnnotation("status-code", e.getStatusCode().value());
            subsegment.addException(e);
            throw new ResponseStatusException(e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Error calling service: " + url, e);
            subsegment.addException(e);
            throw e;
        } finally {
            AWSXRay.endSubsegment();
        }
    }
    
    /**
     * Simplified GET request with automatic trace propagation.
     */
    public <T> T getForObject(String url, Class<T> responseType, String subsegmentName) {
        ResponseEntity<T> response = exchange(url, HttpMethod.GET, null, responseType, subsegmentName);
        return response.getBody();
    }
    
    /**
     * Add X-Ray trace headers to the request headers.
     */
    private void addTraceHeaders(HttpHeaders headers) {
        try {
            // Get current segment
            Segment segment = AWSXRay.getCurrentSegment();
            if (segment != null && segment.getTraceId() != null) {
                // Create X-Ray trace header
                String traceHeader = "Root=" + segment.getTraceId().toString() + 
                                    ";Parent=" + segment.getId() + 
                                    ";Sampled=1";
                headers.set("X-Amzn-Trace-Id", traceHeader);
                logger.debug("Added trace header: {}", traceHeader);
            }
        } catch (Exception e) {
            logger.warn("Could not add trace headers", e);
        }
    }
} 