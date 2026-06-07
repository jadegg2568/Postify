package ru.jadegg2568.Postify.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.views")
@Data
public class ViewProperties {
    private Duration expiration;
    private ViewProperties.Cleanup cleanup;

    @Data
    public static class Cleanup {
        private Duration cleanupDelay;
        private int batchSize;
    }
}
