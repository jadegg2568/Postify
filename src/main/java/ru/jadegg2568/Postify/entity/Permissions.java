package ru.jadegg2568.Postify.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Permissions {
    USER(10, Set.of("ROLE_USER")),
    ADMIN(20, Set.of("ROLE_USER", "ROLE_ADMIN")),
    OWNER(30, Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_OWNER"));

    private final int id;
    private final Set<String> authorities;

    public boolean isAdmin() {
        return authorities.contains("ROLE_ADMIN");
    }
}
