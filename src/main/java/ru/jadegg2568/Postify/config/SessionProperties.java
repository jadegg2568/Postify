package ru.jadegg2568.Postify.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.session")
@Data
public class SessionProperties {

    private String secretKey;

    private Duration expiration;
    private Duration refreshExpiration;

    private SessionProperties.Cleanup cleanup;

    @Data
    public static class Cleanup {
        private Duration delay;
        private int size;
    }
}