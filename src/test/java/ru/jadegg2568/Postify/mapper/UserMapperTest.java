package ru.jadegg2568.Postify.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.response.UserResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Mapper Tests")
class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("toEntity - должен преобразовать RegisterRequest в User игнорируя id, uuid, passwordHash, photoKey, createdAt")
    void toEntity_ShouldMapRegisterRequestToUser() {
        // given
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "rawPassword123",
                "testuser",
                "My Display Name",
                "This is my bio"
        );

        // when
        User result = userMapper.toEntity(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull(); // ignored
        assertThat(result.getUuid()).isNull(); // ignored
        assertThat(result.getMail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("testuser");
        assertThat(result.getPasswordHash()).isNull(); // ignored (not in service layer)
        assertThat(result.getDisplayName()).isEqualTo("My Display Name");
        assertThat(result.getDescription()).isEqualTo("This is my bio");
        assertThat(result.getAvatarKey()).isNull(); // ignored
        assertThat(result.getCreatedAt()).isNull(); // ignored
    }

    @Test
    @DisplayName("toEntity - должен корректно мапить пустые строки")
    void toEntity_ShouldMapEmptyStrings() {
        // given
        RegisterRequest request = new RegisterRequest(
                "empty@example.com",
                "pass",
                "emptyuser",
                "",
                ""
        );

        // when
        User result = userMapper.toEntity(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMail()).isEqualTo("empty@example.com");
        assertThat(result.getName()).isEqualTo("emptyuser");
        assertThat(result.getDisplayName()).isEmpty();
        assertThat(result.getDescription()).isEmpty();
    }

    @Test
    @DisplayName("toResponse - должен преобразовать User в UserResponse")
    void toResponse_ShouldMapUserToUserResponse() {
        // given
        String avatarKey = "s3://bucket/photo.jpg";
        User user = User.builder()
                .uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .mail("user@example.com")
                .name("username")
                .displayName("Display Name")
                .description("User description")
                .avatarKey(avatarKey)
                .build();

        // when
        UserResponse result = userMapper.toResponse(user, avatarKey);

        // then
        assertThat(result).isNotNull();
        assertThat(result.uuid()).isEqualTo(user.getUuid());
        assertThat(result.name()).isEqualTo("username");
        assertThat(result.displayName()).isEqualTo("Display Name");
        assertThat(result.description()).isEqualTo("User description");
    }

    @Test
    @DisplayName("toResponse - должен корректно мапить null значения")
    void toResponse_ShouldHandleNullValues() {
        // given
        User user = User.builder()
                .uuid(null)
                .mail(null)
                .name(null)
                .displayName(null)
                .description(null)
                .avatarKey(null)
                .build();

        // when
        UserResponse result = userMapper.toResponse(user, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.uuid()).isNull();
        assertThat(result.name()).isNull();
        assertThat(result.displayName()).isNull();
        assertThat(result.description()).isNull();
    }

    @Test
    @DisplayName("updateEntity - должен обновить только name, displayName, description игнорируя остальные поля")
    void updateEntity_ShouldUpdateOnlyAllowedFields() {
        // given
        User existingUser = User.builder()
                .id(1L)
                .uuid(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .mail("old@example.com")
                .name("oldname")
                .passwordHash("oldHash")
                .displayName("Old Display")
                .description("Old description")
                .avatarKey("old/photo.jpg")
                .build();

        UpdateProfileRequest request = new UpdateProfileRequest(
                "newname",
                "New Display Name",
                "New description"
        );

        // when
        userMapper.updateEntity(request, existingUser);

        // then
        assertThat(existingUser.getId()).isEqualTo(1L);
        assertThat(existingUser.getUuid()).isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        assertThat(existingUser.getMail()).isEqualTo("old@example.com"); // ignored
        assertThat(existingUser.getPasswordHash()).isEqualTo("oldHash"); // ignored
        assertThat(existingUser.getAvatarKey()).isEqualTo("old/photo.jpg"); // ignored
        
        assertThat(existingUser.getName()).isEqualTo("newname");
        assertThat(existingUser.getDisplayName()).isEqualTo("New Display Name");
        assertThat(existingUser.getDescription()).isEqualTo("New description");
    }

    @Test
    @DisplayName("updateEntity - должен корректно обновлять при пустых значениях")
    void updateEntity_ShouldUpdateWithEmptyValues() {
        // given
        User existingUser = User.builder()
                .name("oldname")
                .displayName("Old Display")
                .description("Old description")
                .build();

        UpdateProfileRequest request = new UpdateProfileRequest(
                "",
                "",
                ""
        );

        // when
        userMapper.updateEntity(request, existingUser);

        // then
        assertThat(existingUser.getName()).isEmpty();
        assertThat(existingUser.getDisplayName()).isEmpty();
        assertThat(existingUser.getDescription()).isEmpty();
    }

//    @Test
//    @DisplayName("updateEntity - должен игнорировать null значения")
//    void updateEntity_ShouldIgnoreNullValues() {
//         given
//        User existingUser = User.builder()
//                .name("oldname")
//                .displayName("Old Display")
//                .description("Old description")
//                .build();
//
//        UpdateProfileRequest request = new UpdateProfileRequest(
//                null,
//                null,
//                null,
//                null
//        );
//
//         when
//        userMapper.updateEntity(request, existingUser);
//
//         then
//        assertThat(existingUser.getName()).isNull();
//        assertThat(existingUser.getDisplayName()).isNull();
//        assertThat(existingUser.getDescription()).isNull();
//    }
}
