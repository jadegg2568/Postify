package ru.jadegg2568.Postify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "postify.session")
@Data
public class SessionProperties {

    private String secretKey;

    private Duration expiration;
    private Duration refreshExpiration;

    private ExpiredDeleting expiredDeleting;

    @Data
    public static class ExpiredDeleting {
        private Duration delay;
        private int count;
    }
}