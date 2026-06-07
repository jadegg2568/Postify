package ru.jadegg2568.Postify.security;

import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.jadegg2568.Postify.config.SessionProperties;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class TokenManager {

    @Autowired
    private SessionProperties sessionProperties;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(sessionProperties.getSecretKey().getBytes());
    }

    public String generateAccessToken(UUID userUuid) {
        return buildToken(
                userUuid.toString(),
                Maps.newHashMap(),
                sessionProperties.getExpiration().toMillis()
        );
    }

    public String generateRefreshToken(UUID userUuid, UUID sessionUuid) {
        return buildToken(
                userUuid.toString(),
                Map.of("sid", sessionUuid.toString()),
                sessionProperties.getRefreshExpiration().toMillis()
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

    public String getSubject(String token) {
        return getClaims(token).getSubject();
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