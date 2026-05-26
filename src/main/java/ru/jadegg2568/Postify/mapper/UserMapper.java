package ru.jadegg2568.Postify.mapper;

import org.mapstruct.*;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.response.UserResponse;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    User toEntity(RegisterRequest registerRequest);

    UserResponse toResponse(User user);

    @Mapping(target = "avatarUrl", expression = "java(avatarUrl)")
    UserResponse toResponse(User user, String avatarUrl);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateProfileRequest request, @MappingTarget User user);
}
