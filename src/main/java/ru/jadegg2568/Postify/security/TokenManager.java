package ru.jadegg2568.Postify.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.jadegg2568.Postify.config.JwtProperties;
import ru.jadegg2568.Postify.entity.Permissions;
import ru.jadegg2568.Postify.entity.User;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class TokenManager {

    @Autowired
    private JwtProperties jwtProperties;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String generateAccessToken(UUID userUuid, Set<String> roles) {
        return buildToken(
                userUuid.toString(),
                Map.of("roles", roles),
                jwtProperties.getExpirationTime().toMillis()
        );
    }

    public String generateRefreshToken(UUID userUuid, UUID sessionUuid) {
        return buildToken(
                userUuid.toString(),
                Map.of("sid", sessionUuid.toString()),
                jwtProperties.getRefreshExpirationTime().toMillis()
        );
    }

    private String buildToken(String subject, Map<String, Object> claims, long expirationMs) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSecretKey())
                .compact();
    }

    public <T> T getClaim(String token, String name, Class<T> type) {
        return getClaims(token).get(name, type);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}