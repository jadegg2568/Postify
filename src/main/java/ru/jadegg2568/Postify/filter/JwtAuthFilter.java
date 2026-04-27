package ru.jadegg2568.Postify.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.security.TokenManager;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private TokenManager jwtService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtService.getClaims(token);
                String userUuid = claims.getSubject();
                List<?> rawRoles = claims.get("roles", List.class);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if (rawRoles != null) {
                    authorities = rawRoles.stream()
                            .filter(obj -> obj instanceof String)
                            .map(obj -> new SimpleGrantedAuthority((String) obj))
                            .collect(Collectors.toList());
                }

                if (userUuid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UuidUserDetails details = new UuidUserDetails(UUID.fromString(userUuid), authorities);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            details, null, details.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                handlerExceptionResolver.resolveException(request, response, null, e);
                logger.debug("Could not set user authentication in security context", e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}