package ru.jadegg2568.Postify.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.jadegg2568.Postify.parse.Device;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.AuthMapper;
import ru.jadegg2568.Postify.mapper.UserMapper;
import ru.jadegg2568.Postify.request.LoginRequest;
import ru.jadegg2568.Postify.request.RefreshRequest;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.response.AuthResponse;
import ru.jadegg2568.Postify.response.SessionRefreshResponse;
import ru.jadegg2568.Postify.response.UserResponse;
import ru.jadegg2568.Postify.service.AuthService;
import ru.jadegg2568.Postify.service.FileService;
import ru.jadegg2568.Postify.service.SessionService;
import ru.jadegg2568.Postify.parse.UserAgentParser;

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
    private final FileService fileService;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final UserAgentParser userAgentParser;

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
    public ResponseEntity<AuthResponse> register(HttpServletRequest servletRequest,
                                                 @Valid @RequestBody RegisterRequest request) {
        Device device = userAgentParser.parseDevice(
                servletRequest.getHeader("User-Agent"));
        User user = authService.register(request);
        return createSessionAndTokens(user, device);
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
    public ResponseEntity<AuthResponse> login(HttpServletRequest servletRequest,
                                              @Valid @RequestBody LoginRequest request) {
        Device device = userAgentParser.parseDevice(
                servletRequest.getHeader("User-Agent"));
        User user = authService.login(request);
        return createSessionAndTokens(user, device);
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

    private @NonNull ResponseEntity<AuthResponse> createSessionAndTokens(User user, Device device) {
        // generate session
        Session session = sessionService.generateSession(user, device);

        // generate refresh and access tokens
        String refreshToken = sessionService.generateRefreshToken(session);
        String token = sessionService.generateToken(user, session);

        // create response with tokens, session and user data
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authMapper.toAuthResponse(session, refreshToken, token, getUserResponse(user)));
    }

    private @NonNull UserResponse getUserResponse(User user) {
        // convert avatarKey into avatarUrl
        String avatarKey = user.getAvatarKey();
        String avatarUrl = (avatarKey != null) ? fileService.generatePresignedUrl(avatarKey) : null;
        // map from user text data and avatarUrl
        return userMapper.toResponse(user, avatarUrl);
    }
}
