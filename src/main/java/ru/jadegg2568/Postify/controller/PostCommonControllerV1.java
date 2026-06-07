package ru.jadegg2568.Postify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.mapper.PostMapper;
import ru.jadegg2568.Postify.request.PostCreateRequest;
import ru.jadegg2568.Postify.request.PostUpdateRequest;
import ru.jadegg2568.Postify.response.PostResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.PostService;

import java.util.UUID;

@Tag(
        name = "Post Controller V1",
        description = "Posts API (authentication required)"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostCommonControllerV1 {
    private final PostService postService;
    private final PostMapper postMapper;

    @Operation(
            summary = "Create post",
            description = "Creates a new post. Optional reply={uuid} creates a reply to another post"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Post created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (validation failed)"),
            @ApiResponse(responseCode = "404", description = "Reply post not found")
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponse> create(
            @AuthenticationPrincipal UuidUserDetails details,
            @Valid @RequestBody PostCreateRequest request,
            @RequestParam(required = false, name = "reply") UUID replyToUuid
    ) {
        Post post = postService.create(details.uuid(), request, replyToUuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toResponse(post));
    }

    @Operation(
            summary = "Update post",
            description = "Updates post fields. Only author (or admin) can update"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (validation failed)"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @PatchMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN') or @postService.isAuthor(#uuid, principal.uuid())")
    public ResponseEntity<PostResponse> update(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        Post post = postService.update(details.uuid(), uuid, request);
        return ResponseEntity.ok(postMapper.toResponse(post));
    }

    @Operation(
            summary = "Delete post",
            description = "Deletes a post. Only author (or admin) can delete"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post deleted"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN') or @postService.isAuthor(#uuid, principal.uuid())")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UuidUserDetails details,
            @PathVariable UUID uuid
    ) {
        postService.delete(details.uuid(), uuid);
        return ResponseEntity.noContent().build();
    }
}