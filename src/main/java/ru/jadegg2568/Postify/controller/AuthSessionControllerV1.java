package ru.jadegg2568.Postify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.mapper.SessionMapper;
import ru.jadegg2568.Postify.response.SessionResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.SessionService;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Auth Session Controller V1",
        description = "User session management API"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
@RestController
@RequestMapping("/v1/auth/sessions")
@RequiredArgsConstructor
public class AuthSessionControllerV1 {

    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    @Operation(
            summary = "List sessions",
            description = "Returns all sessions for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessions returned"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SessionResponse>> listSessions(
            @AuthenticationPrincipal UuidUserDetails details) {
        List<SessionResponse> sessions = sessionService.getSessions(details.uuid()).stream()
                .map(sessionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @Operation(
            summary = "Get session by UUID",
            description = "Returns a session by its UUID for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session returned"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @GetMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SessionResponse> getSession(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid) {
        Session session = sessionService.getSession(details.uuid(), uuid);
        return ResponseEntity.ok(sessionMapper.toResponse(session));
    }

    @Operation(
            summary = "Revoke all sessions",
            description = "Cancels all sessions for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All sessions revoked"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> revokeAllSessions(@AuthenticationPrincipal UuidUserDetails details) {
        sessionService.revokeSessions(details.uuid());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Revoke session by UUID",
            description = "Cancels a session by its UUID for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Session revoked"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @DeleteMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> revokeSession(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid) {
        sessionService.revokeSession(details.uuid(), uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
