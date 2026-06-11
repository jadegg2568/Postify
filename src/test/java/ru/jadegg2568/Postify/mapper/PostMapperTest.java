package ru.jadegg2568.Postify.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.request.PostCreateRequest;
import ru.jadegg2568.Postify.request.PostUpdateRequest;
import ru.jadegg2568.Postify.response.PostResponse;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Post Mapper Tests")
class PostMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final PostMapper postMapper = wirePostMapper();

    @Test
    @DisplayName("toEntity - должен преобразовать PostCreateRequest в Post (title/content), игнорируя id/uuid/author/replyTo/createdAt")
    void toEntity_ShouldMapCreateRequestToPost() {
        // given
        PostCreateRequest request = new PostCreateRequest("Hello", "World");

        // when
        Post result = postMapper.toEntity(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getUuid()).isNull();
        assertThat(result.getAuthor()).isNull();
        assertThat(result.getReplyTo()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getTitle()).isEqualTo("Hello");
        assertThat(result.getContent()).isEqualTo("World");
    }

    @Test
    @DisplayName("updateEntity - должен обновлять только не-null поля")
    void updateEntity_ShouldUpdateOnlyNonNullFields() {
        // given
        Post post = Post.builder()
                .title("old title")
                .content("old content")
                .build();

        // when
        postMapper.updateEntity(new PostUpdateRequest("new title", null), post);

        // then
        assertThat(post.getTitle()).isEqualTo("new title");
        assertThat(post.getContent()).isEqualTo("old content");
    }

    @Test
    @DisplayName("toResponse - должен мапить replyToUuid из replyTo.uuid и автора через UserMapper")
    void toResponse_ShouldMapReplyToUuidAndAuthor() {
        // given
        UUID authorUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID postUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        UUID replyUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        Instant createdAt = Instant.parse("2026-05-05T18:20:00Z");

        User author = User.builder()
                .uuid(authorUuid)
                .name("author")
                .displayName("Author Display")
                .description("Bio")
                .build();

        Post replyTo = Post.builder()
                .uuid(replyUuid)
                .title("parent")
                .content("parent content")
                .author(author)
                .build();

        Post post = Post.builder()
                .uuid(postUuid)
                .author(author)
                .replyTo(replyTo)
                .title("title")
                .content("content")
                .views(42L)
                .createdAt(createdAt)
                .build();

        // when
        PostResponse result = postMapper.toResponse(post);

        // then
        assertThat(result).isNotNull();
        assertThat(result.uuid()).isEqualTo(postUuid);
        assertThat(result.replyToUuid()).isEqualTo(replyUuid);
        assertThat(result.title()).isEqualTo("title");
        assertThat(result.content()).isEqualTo("content");
        assertThat(result.views()).isEqualTo(42L);
        assertThat(result.createdAt()).isEqualTo(createdAt);

        assertThat(result.author()).isNotNull();
        assertThat(result.author().uuid()).isEqualTo(authorUuid);
        assertThat(result.author().name()).isEqualTo("author");
        assertThat(result.author().displayName()).isEqualTo("Author Display");
        assertThat(result.author().description()).isEqualTo("Bio");
    }

    @Test
    @DisplayName("toResponse - должен мапить null replyToUuid когда replyTo=null")
    void toResponse_ShouldHandleNullReplyTo() {
        // given
        User author = User.builder().uuid(UUID.randomUUID()).name("author").build();
        Post post = Post.builder()
                .uuid(UUID.randomUUID())
                .author(author)
                .replyTo(null)
                .title("t")
                .content("c")
                .build();

        // when
        PostResponse result = postMapper.toResponse(post);

        // then
        assertThat(result.replyToUuid()).isNull();
    }

    private PostMapper wirePostMapper() {
        PostMapper mapper = Mappers.getMapper(PostMapper.class);
        injectFieldByType(mapper, UserMapper.class, userMapper);
        return mapper;
    }

    private static void injectFieldByType(Object target, Class<?> fieldType, Object value) {
        for (Field f : target.getClass().getDeclaredFields()) {
            if (fieldType.isAssignableFrom(f.getType())) {
                try {
                    f.setAccessible(true);
                    f.set(target, value);
                    return;
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to inject " + fieldType.getSimpleName(), e);
                }
            }
        }
        throw new IllegalStateException("No field of type " + fieldType.getSimpleName() + " found on " + target.getClass().getName());
    }
}

