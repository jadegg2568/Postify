package ru.jadegg2568.Postify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.response.AuthResponse;
import ru.jadegg2568.Postify.response.UserResponse;

@Mapper(
        componentModel = "spring",
        uses = SessionMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AuthMapper {

    @Mapping(target = "uuid", source = "session.user.uuid")
    @Mapping(target = "session", source = "session")
    @Mapping(target = "data", source = "data")
    AuthResponse toAuthResponse(Session session, String refreshToken, String token, UserResponse data);
}
