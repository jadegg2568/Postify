package ru.jadegg2568.Postify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "postify.views")
@Data
public class ViewProperties {
    private Duration cleanupDelay;
    private Duration retentionAge;
    private int batchSize;
}
