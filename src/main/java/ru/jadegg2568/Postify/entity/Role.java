package ru.jadegg2568.Postify.entity;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String str;

    Role(String str) {
        this.str = str;
    }
}
