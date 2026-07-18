package ru.jadegg2568.Postify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.session")
@Data
public class SessionConfig {

    private String secretKey;

    private Duration accessExpiration;
    private Duration refreshExpiration;

    private Cleanup cleanup;

}