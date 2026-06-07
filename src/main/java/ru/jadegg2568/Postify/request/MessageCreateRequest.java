package ru.jadegg2568.Postify.request;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageCreateRequest {
    private String text;
}