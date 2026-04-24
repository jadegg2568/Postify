package ru.jadegg2568.Postify.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Rights;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.exception.auth.NoAccessException;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.response.TokenResponse;
import ru.jadegg2568.Postify.security.UuidUserDetails;
import ru.jadegg2568.Postify.service.AuthService;
import ru.jadegg2568.Postify.service.UserService;

import java.util.UUID;

@Tag(
        name = "Auth Controller V1",
        description = "User auth operations API"
)
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final AuthService authService;
    private final UserMapper userMapper;

    // public
    // POST /register
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with unique login/email constraints"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (validation failed)"),
            @ApiResponse(responseCode = "409", description = "User already exists with given unique fields")
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        String token = authService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(token, user.getUuid(), userMapper.toResponse(user)));
    }

    // public
    // POST /login
    @Operation(
            summary = "Login user",
            description = "Authenticates user using login and password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials format or wrong data")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        String token = authService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(token, user.getUuid(), userMapper.toResponse(user)));
    }
}
