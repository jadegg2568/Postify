package ru.jadegg2568.Postify.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Permissions {
    USER(10), // standard user
    ADMIN(20), // admin
    OWNER(30); // owner (can manage permissions)

    private final int id;

    public boolean isAdmin() {
        return id >= ADMIN.id;
    }

    public Set<String> authorities() {
        return switch (this) {
            case USER -> Set.of("ROLE_USER");
            case ADMIN -> Set.of("ROLE_USER", "ROLE_ADMIN");
            case OWNER -> Set.of("ROLE_USER", "ROLE_ADMIN", "ROLE_OWNER");
        };
    }
}
