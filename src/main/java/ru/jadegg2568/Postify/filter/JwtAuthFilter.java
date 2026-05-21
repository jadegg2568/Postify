package ru.jadegg2568.Postify.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.jadegg2568.Postify.security.TokenManager;
import ru.jadegg2568.Postify.security.UuidUserDetails;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private TokenManager jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/actuator/",
            "/v1/auth/register",
            "/v1/auth/login",
            "/swagger-ui/",
            "/v3/api-docs/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Проверяем, публичный ли путь
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Проверяем GET запросы на /v1/users и /v1/posts
        if ((path.startsWith("/v1/users/") || path.startsWith("/v1/posts/")) &&
                "GET".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.getClaims(token);
            String userUuid = claims.getSubject();

            if (userUuid == null) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token: missing subject");
                return;
            }

            List<SimpleGrantedAuthority> authorities = extractAuthorities(claims);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UuidUserDetails userDetails = new UuidUserDetails(UUID.fromString(userUuid), authorities);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            logger.debug("JWT token expired: {}", e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT token has expired. Please login again.");
        } catch (JwtException e) {
            logger.debug("Invalid JWT token: {}", e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
        } catch (Exception e) {
            logger.error("Unexpected error during JWT processing", e);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error during authentication");
        }
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        List<?> rawRoles = claims.get("roles", List.class);
        if (rawRoles == null) {
            return Collections.emptyList();
        }

        return rawRoles.stream()
                .filter(role -> role instanceof String)
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toList());
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("code", status == HttpStatus.UNAUTHORIZED ? "UNAUTHORIZED" : "AUTH_ERROR");
        errorResponse.put("message", message);
        errorResponse.put("traceId", UUID.randomUUID().toString());
        errorResponse.put("timestamp", Instant.now().toString());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}