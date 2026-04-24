package ru.jadegg2568.Postify.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Rights {
    USER(Set.of("ROLE_USER")),
    ADMIN(Set.of("ROLE_USER", "ROLE_ADMIN")),
    OWNER(Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_OWNER"));

    private final Set<String> authorities;

    public boolean isAdmin() {
        return authorities.contains("ROLE_ADMIN");
    }
}
