package ru.jadegg2568.Postify.auth;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SessionMapper {

    SessionResponse toResponse(Session session);
}
