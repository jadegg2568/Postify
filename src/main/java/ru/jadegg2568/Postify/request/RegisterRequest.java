package ru.jadegg2568.Postify.request;

import jakarta.annotation.Nullable;

public record RegisterRequest(String mail,
                              String password,
                              String name,
                              String displayName,
                              @Nullable String description) { // not important field
}