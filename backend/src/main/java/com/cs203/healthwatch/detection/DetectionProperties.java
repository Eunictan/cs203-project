package com.cs203.healthwatch.detection;

import org.springframework.boot.context.properties.ConfigurationProperties; 

@ConfigurationProperties(prefix = "app.detection")
public record DetectionProperties(
        double zThreshold,
        int consecutiveReadings
) {}