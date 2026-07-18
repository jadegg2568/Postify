package ru.jadegg2568.Postify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua_parser.Parser;

@Configuration
public class WebConfig {

    // to parse user agent which contains os and browser
    @Bean
    public Parser userAgentParser() {
        return new Parser();
    }
}
