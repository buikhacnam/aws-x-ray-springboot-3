package com.example.user.config;

import org.springframework.context.annotation.Configuration;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.slf4j.SLF4JSegmentListener;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;

@Configuration
public class XRayConfig {

    static {
        // Create a simpler recorder builder
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard();

        // Load sampling rules
        builder.withSamplingStrategy(new LocalizedSamplingStrategy(XRayConfig.class.getClassLoader().getResource("sampling-rules.json")));

        // Add SLF4J segment listener for trace ID logging
        builder.withSegmentListener(new SLF4JSegmentListener());

        // Set the global recorder
        AWSXRay.setGlobalRecorder(builder.build());
    }

} 