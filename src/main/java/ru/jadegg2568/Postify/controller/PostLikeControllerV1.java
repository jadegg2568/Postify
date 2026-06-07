package ru.jadegg2568.Postify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.PostMapper;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.response.LikeResponse;
import ru.jadegg2568.Postify.response.PostResponse;
import ru.jadegg2568.Postify.response.UserResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.FileService;
import ru.jadegg2568.Postify.service.LikeService;
import ru.jadegg2568.Postify.service.PostService;
import ru.jadegg2568.Postify.service.UserService;

import java.util.UUID;

@Tag(
        name = "Post Like Controller V1",
        description = "Post likes API. GET is public, POST/DELETE require authentication"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
@RestController
@RequestMapping("/v1/posts/{uuid}/like")
@RequiredArgsConstructor
public class PostLikeControllerV1 {
    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final FileService fileService;

    @Operation(
            summary = "Like post",
            description = "Creates a like for the specified post from the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post liked successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponse> like(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid
    ) {
        User user = userService.getByUuid(details.uuid());
        Post post = postService.getByUuid(uuid);
        Post result = likeService.like(user, post);
        return ResponseEntity.ok(postMapper.toResponse(result));
    }

    @Operation(
            summary = "Unlike post",
            description = "Removes a placed like from the specified post for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Like removed successfully. Returns updated post state."),
            @ApiResponse(responseCode = "404", description = "Post or like not found")
    })
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponse> dislike(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid
    ) {
        User user = userService.getByUuid(details.uuid());
        Post post = postService.getByUuid(uuid);
        Post result = likeService.unlike(user, post);
        return ResponseEntity.ok(postMapper.toResponse(result));
    }

    @Operation(
            summary = "Get post likes",
            description = """
                    Returns likes for the specified post.
                    When show_users=false (default), only count is returned.
                    When show_users=true, returns count and list of users who liked the post.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Likes returned"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping
    public ResponseEntity<LikeResponse> getLikes(
            @PathVariable UUID uuid,
            @RequestParam(defaultValue = "false") boolean show_users
    ) {
        Post post = postService.getByUuid(uuid);
        long count = likeService.getLikesCount(post);
        if (!show_users) {
            return ResponseEntity.ok(new LikeResponse(count, null));
        }

        var users = likeService.getLikedUsers(post).stream()
                .map(this::getUserResponse)
                .toList();
        return ResponseEntity.ok(new LikeResponse(count, users));
    }

    private @NonNull UserResponse getUserResponse(User user) {
        String avatarKey = user.getAvatarKey();
        String avatarUrl = (avatarKey != null) ? fileService.getPresignedUrl(avatarKey) : null;
        return userMapper.toResponse(user, avatarUrl);
    }
}
