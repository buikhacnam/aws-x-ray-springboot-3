package com.example.user;

import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@XRayEnabled
public class UserApplication {

	public static void main(String[] args) {
		System.setProperty("com.amazonaws.xray.strategy.tracingName", "user-service");
		System.setProperty("com.amazonaws.xray.tracing.servlet.enableDefaultSegmentNaming", "true");
		SpringApplication.run(UserApplication.class, args);
	}


	@Bean
	public AWSXRayServletFilter tracingFilter() {
		return new AWSXRayServletFilter("user-service");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
