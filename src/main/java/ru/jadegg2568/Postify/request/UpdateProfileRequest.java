package ru.jadegg2568.Postify.request;

public record UpdateProfileRequest(String name,
                                  String title,
                                  String displayName) {
}
