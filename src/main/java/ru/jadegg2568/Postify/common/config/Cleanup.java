package ru.jadegg2568.Postify.common.config;

import lombok.Data;

import java.time.Duration;

@Data
public class Cleanup {
    private Duration expiration;
    private Duration delay;
    private int size;
}