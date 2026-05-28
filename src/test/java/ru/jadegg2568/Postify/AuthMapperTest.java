package ru.jadegg2568.Postify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.AuthMapper;
import ru.jadegg2568.Postify.mapper.AuthMapperImpl;
import ru.jadegg2568.Postify.mapper.SessionMapperImpl;
import ru.jadegg2568.Postify.response.AuthResponse;
import ru.jadegg2568.Postify.response.UserResponse;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = {SessionMapperImpl.class, AuthMapperImpl.class})
@DisplayName("Auth Mapper Tests")
class AuthMapperTest {

    @Autowired
    private AuthMapper authMapper;

    @Test
    @DisplayName("toAuthResponse - должен собрать AuthResponse с токенами, сессией и пользователем")
    void toAuthResponse_ShouldMapAuthResponse() {
        // given
        UUID userUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID sessionUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        User user = User.builder().uuid(userUuid).build();
        Session session = Session.builder()
                .uuid(sessionUuid)
                .user(user)
                .browser("Chrome 148")
                .os("Windows 10 22H2")
                .cancelled(false)
                .expiresAt(Instant.parse("2026-06-01T12:00:00Z"))
                .createdAt(Instant.parse("2026-05-01T12:00:00Z"))
                .build();

        UserResponse userResponse = new UserResponse(
                userUuid,
                "username",
                "Display Name",
                "Bio",
                "https://avatar.example/photo.jpg"
        );

        // when
        AuthResponse result = authMapper.toAuthResponse(
                session,
                "refresh-token",
                "access-token",
                userResponse
        );

        // then
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.token()).isEqualTo("access-token");
        assertThat(result.uuid()).isEqualTo(userUuid);
        assertThat(result.data()).isEqualTo(userResponse);
        assertThat(result.session().uuid()).isEqualTo(sessionUuid);
    }
}
