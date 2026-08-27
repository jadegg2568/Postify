package ru.jadegg2568.Postify.dialogue;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.jadegg2568.Postify.user.UserMapper;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MessageMapper {

    @Mapping(target = "replyTo", source = "replyTo.uuid")
    MessageResponse toResponse(Message response);
}