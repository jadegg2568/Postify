package ru.jadegg2568.Postify.post;

import org.mapstruct.*;
import ru.jadegg2568.Postify.user.UserMapper;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    Post toEntity(PostCreateRequest request);

    @Mapping(target = "replyToUuid", source = "replyTo.uuid")
    PostResponse toResponse(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PostUpdateRequest request, @MappingTarget Post post);
}