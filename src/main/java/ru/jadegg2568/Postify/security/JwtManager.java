package ru.jadegg2568.Postify.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.jadegg2568.Postify.config.JwtProperties;
import ru.jadegg2568.Postify.entity.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtManager {

    @Autowired
    private JwtProperties jwtProperties;

    private @NonNull SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String toToken(UUID uuid, Role role) {
        return Jwts.builder()
                .subject(uuid.toString())
                .claim("role", role.getStr()) // role
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime().toMillis())) // date of expiration
                .signWith(getSecretKey())
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
