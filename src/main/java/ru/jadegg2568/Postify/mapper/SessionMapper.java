package ru.jadegg2568.Postify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.response.SessionResponse;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SessionMapper {

    @Mapping(target = "userUuid", source = "user.uuid")
    SessionResponse toResponse(Session session);
}
