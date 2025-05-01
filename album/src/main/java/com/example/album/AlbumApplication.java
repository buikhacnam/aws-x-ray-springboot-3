package com.example.album;


import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@XRayEnabled
public class AlbumApplication {

	public static void main(String[] args) {

		System.setProperty("com.amazonaws.xray.strategy.tracingName", "album-service");
		System.setProperty("com.amazonaws.xray.tracing.servlet.enableDefaultSegmentNaming", "true");
		SpringApplication.run(AlbumApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public AWSXRayServletFilter tracingFilter() {
		return new AWSXRayServletFilter("album-service");
	}
}
