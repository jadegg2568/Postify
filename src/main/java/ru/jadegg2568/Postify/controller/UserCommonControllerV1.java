package ru.jadegg2568.Postify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.response.UserResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.UserService;

import java.util.UUID;

@Tag(
        name = "User Controller V1",
        description = "User API (authentication required)"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserCommonControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Update user profile",
            description = "Updates user profile data for authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (invalid or missing auth)")
    })
    @PatchMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN') or #uuid == authentication.principal.uuid")
    public ResponseEntity<UserResponse> update(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateProfileRequest request) {
        User user = userService.updateProfile(uuid, request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes user account by UUID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN') or #uuid == authentication.principal.uuid")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UuidUserDetails details, @PathVariable UUID uuid) {
        userService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}