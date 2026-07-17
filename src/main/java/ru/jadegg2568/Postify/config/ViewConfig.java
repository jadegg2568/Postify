package ru.jadegg2568.Postify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration("viewProperties")
@ConfigurationProperties(prefix = "app.views")
@Data
public class ViewConfig {
    private Duration expiration;
    private ViewConfig.Cleanup cleanup;

    @Data
    public static class Cleanup {
        private Duration cleanupDelay;
        private int batchSize;
    }
}
