package ru.jadegg2568.Postify.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.jadegg2568.Postify.entity.Rights;

import java.util.Collection;
import java.util.UUID;

public record UuidUserDetails(UUID uuid, Rights rights)
        implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rights.getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getUsername() {
        return uuid.toString();
    }

    @Override
    public String getPassword() {
        return "";
    }
}