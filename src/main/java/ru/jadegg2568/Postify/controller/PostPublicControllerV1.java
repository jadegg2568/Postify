package ru.jadegg2568.Postify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.PostMapper;
import ru.jadegg2568.Postify.response.PostResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.PostService;
import ru.jadegg2568.Postify.service.UserService;
import ru.jadegg2568.Postify.service.ViewService;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Post Public Controller V1",
        description = "Public Posts API (no authentication required)"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error")
})
@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostPublicControllerV1 {
    private final PostService postService;
    private final UserService userService;
    private final ViewService viewService;
    private final PostMapper postMapper;

    @Operation(
            summary = "Get post by UUID",
            description = "Returns a post by UUID. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post found"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<PostResponse> getByUuid(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal UuidUserDetails details
    ) {
        Post post = postService.getByUuid(uuid);
        trackViewIfAuthenticated(details, post);
        return ResponseEntity.ok(postMapper.toResponse(post));
    }

    @Operation(
            summary = "Search posts by title",
            description = "Returns posts found by title. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Posts found"),
    })
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "10") int length,
            @AuthenticationPrincipal UuidUserDetails details
    ) {
        Page<Post> posts = (title != null)
                ? postService.searchByTitle(title, length)
                : postService.find(length);

        if (details != null && !posts.isEmpty()) {
            User user = userService.getByUuid(details.uuid());
            for (Post post : posts) {
                viewService.viewedPost(user, post);
            }
        }

        return ResponseEntity.ok(posts.stream().map(postMapper::toResponse).toList());
    }

    private void trackViewIfAuthenticated(UuidUserDetails details, Post post) {
        if (details == null) {
            return;
        }
        User user = userService.getByUuid(details.uuid());
        viewService.viewedPost(user, post);
    }
}
