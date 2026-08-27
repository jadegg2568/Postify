package ru.jadegg2568.Postify.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.file.FileService;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "User Public Controller V1",
        description = "Public User API (no authentication required)"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserPublicControllerV1 {
    private final UserService userService;
    private final UserMapper userMapper;
    private final FileService fileService;

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
        return ResponseEntity.ok(getUserResponse(user));
    }

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
        return ResponseEntity.ok(getUserResponse(user));
    }

    @Operation(
            summary = "Search users",
            description = "Search users by partial query string"
    )
    @ApiResponse(responseCode = "200", description = "List of matching users")
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q) {
        List<User> users = userService.searchUsers(q);
        return ResponseEntity.ok(users.stream().map(this::getUserResponse).toList());
    }

    private @NonNull UserResponse getUserResponse(User user) {
        String avatarKey = user.getAvatarKey();
        String avatarUrl = (avatarKey != null) ? fileService.generatePresignedUrl(avatarKey) : null;
        return userMapper.toResponse(user, avatarUrl);
    }
}