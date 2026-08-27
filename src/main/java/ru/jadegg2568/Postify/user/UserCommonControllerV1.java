package ru.jadegg2568.Postify.user;

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
import org.springframework.web.multipart.MultipartFile;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.file.FileService;

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
    private final FileService fileService;

    @Operation(
            summary = "Update current user profile",
            description = "Updates profile data for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> update(
            @AuthenticationPrincipal UuidUserDetails details,
            @Valid @RequestBody UpdateProfileRequest request) {
        User user = userService.updateProfile(details.uuid(), request);
        String avatarKey = user.getAvatarKey();
        String avatarUrl = (avatarKey != null) ? fileService.generatePresignedUrl(avatarKey) : null;
        return ResponseEntity.ok(userMapper.toResponse(user, avatarUrl));
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<String> updateAvatar(
            @AuthenticationPrincipal UuidUserDetails details,
            @RequestParam("file") MultipartFile file) {
        User user = userService.getByUuid(details.uuid());
        String key = fileService.uploadFile(user, file);
        String url = userService.updateAvatarKey(user, key);
        return ResponseEntity.ok(url);
    }

    @Operation(
            summary = "Delete current user account",
            description = "Permanently deletes the authenticated user's account. This action is irreversible."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteSelf(@AuthenticationPrincipal UuidUserDetails details) {
        userService.delete(details.uuid());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete user by UUID (Admin only)",
            description = "Deletes any user account by UUID. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByAdmin(@PathVariable UUID uuid) {
        userService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}