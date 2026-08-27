package ru.jadegg2568.Postify.post;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.jadegg2568.Postify.common.config.Cleanup;

@Configuration("viewProperties")
@ConfigurationProperties(prefix = "app.views")
@Data
public class PostViewConfig {
    private Cleanup cleanup;
}
