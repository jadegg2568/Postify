package ru.jadegg2568.Postify.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.response.UserResponse;
import ru.jadegg2568.Postify.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;

    // POST /register
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(user));
    }

    // POST /login
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    // GET /{uuid}
    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getUserByUuid(@PathVariable UUID uuid) {
        User user = userService.getByUuid(uuid);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    // PUT /{uuid}
    @PutMapping("/{uuid}")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateProfileRequest request) {
        User user = userService.updateProfile(uuid, request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    // DELETE /{uuid}
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        userService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    // GET /search?q={query}
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q) {
        List<User> users = userService.searchUsers(q);
        return ResponseEntity.ok(users.stream().map(userMapper::toResponse).toList());
    }
}