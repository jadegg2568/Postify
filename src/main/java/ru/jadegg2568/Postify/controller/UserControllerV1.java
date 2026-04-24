package ru.jadegg2568.Postify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Role;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.NoAccessException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.response.UserResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.UserService;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "User Controller V1",
        description = "User non-auth operations API"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;

    // Patch /{uuid}
    @Operation(
            summary = "Update user profile",
            description = "Updates user profile data for authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized (invalid or missing auth)")
    })
    @PatchMapping("/{uuid}")
    public ResponseEntity<UserResponse> update(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateProfileRequest request) {
        if (!details.role().equals(Role.ADMIN.getAuthority()) && !details.uuid().equals(uuid)) {
            throw new NoAccessException();
        }
        User user = userService.updateProfile(uuid, request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    // public
    // GET /{uuid}WE
    @Operation(
            summary = "Get user by UUID",
            description = "Returns user information by unique identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getUserByUuid(@PathVariable UUID uuid) {
        User user = userService.getByUuid(uuid);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    // public
    // GET ?name={name}
    @Operation(
            summary = "Get user by name",
            description = "Finds user by unique username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<UserResponse> getUserByName(@RequestParam String name) {
        User user = userService.getByName(name);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    // public
    // GET /search?q={query}
    @Operation(
            summary = "Search users",
            description = "Search users by partial query string"
    )
    @ApiResponse(responseCode = "200", description = "List of matching users")
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q) {
        List<User> users = userService.searchUsers(q);
        return ResponseEntity.ok(users.stream().map(userMapper::toResponse).toList());
    }
}