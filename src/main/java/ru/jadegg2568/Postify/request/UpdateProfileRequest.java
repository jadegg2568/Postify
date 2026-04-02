package ru.jadegg2568.Postify.request;

public record UpdateProfileRequest(String name,
                                  String displayName,
                                  String description) {
}
