package ru.jadegg2568.Postify.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record UuidUserDetails(UUID uuid, List<SimpleGrantedAuthority> roles)
        implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
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