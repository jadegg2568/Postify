package ru.jadegg2568.Postify.mapper;

import org.mapstruct.*;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.request.RegisterRequest;
import ru.jadegg2568.Postify.request.UpdateProfileRequest;
import ru.jadegg2568.Postify.response.UserResponse;

@Mapper
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "photoKey", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterRequest registerRequest);
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "mail", ignore = true)        // split
    @Mapping(target = "passwordHash", ignore = true) // split
    @Mapping(target = "photoKey", ignore = true)     // split
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // ignore null values
    void updateEntity(UpdateProfileRequest request, @MappingTarget User user);
}
