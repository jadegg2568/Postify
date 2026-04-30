package ru.jadegg2568.Postify.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RefreshRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.response.SessionRefreshResponse;
import ru.jadegg2568.Postify.response.SessionResponse;
import ru.jadegg2568.Postify.service.AuthService;
import ru.jadegg2568.Postify.service.SessionService;

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
    private final SessionService sessionService;
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
    public ResponseEntity<SessionResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return createSessionAndTokens(user);
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
    public ResponseEntity<SessionResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return createSessionAndTokens(user);
    }

    // public
    // POST /refresh
    @Operation(
            summary = "Refresh token",
            description = "Gives a new access token from refresh token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "404", description = "Not found session"),
            @ApiResponse(responseCode = "401", description = "Session is already expired or cancelled"),
    })
    @PostMapping("/refresh")
    public ResponseEntity<SessionRefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String token = sessionService.generateToken(request.refreshToken());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SessionRefreshResponse(token));
    }

    private @NonNull ResponseEntity<SessionResponse> createSessionAndTokens(User user) {
        Session session = sessionService.generateSession(user);

        String refreshToken = sessionService.generateRefreshToken(session);
        String token = sessionService.generateToken(user, session);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SessionResponse(refreshToken, token, user.getUuid(), userMapper.toResponse(user)));
    }
}
