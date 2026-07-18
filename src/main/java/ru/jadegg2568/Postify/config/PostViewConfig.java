package ru.jadegg2568.Postify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("viewProperties")
@ConfigurationProperties(prefix = "app.views")
@Data
public class PostViewConfig {
    private Cleanup cleanup;
}
