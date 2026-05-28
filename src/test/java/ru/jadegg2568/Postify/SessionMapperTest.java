package ru.jadegg2568.Postify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.mapper.SessionMapper;
import ru.jadegg2568.Postify.response.SessionResponse;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Session Mapper Tests")
class SessionMapperTest {

    private final SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

    @Test
    @DisplayName("toResponse - должен преобразовать Session в SessionResponse")
    void toResponse_ShouldMapSessionToSessionResponse() {
        // given
        UUID userUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID sessionUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        Instant expiresAt = Instant.parse("2026-06-01T12:00:00Z");
        Instant createdAt = Instant.parse("2026-05-01T12:00:00Z");

        User user = User.builder().uuid(userUuid).build();
        Session session = Session.builder()
                .uuid(sessionUuid)
                .user(user)
                .title("device-1")
                .cancelled(true)
                .expiresAt(expiresAt)
                .createdAt(createdAt)
                .build();

        // when
        SessionResponse result = sessionMapper.toResponse(session);

        // then
        assertThat(result.uuid()).isEqualTo(sessionUuid);
        assertThat(result.title()).isEqualTo("device-1");
        assertThat(result.cancelled()).isTrue();
        assertThat(result.expiresAt()).isEqualTo(expiresAt);
        assertThat(result.createdAt()).isEqualTo(createdAt);
    }
}
