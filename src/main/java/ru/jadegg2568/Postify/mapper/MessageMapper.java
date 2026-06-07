package ru.jadegg2568.Postify.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.Message;
import ru.jadegg2568.Postify.response.MessageResponse;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MessageMapper {

    @Mapping(target = "sender", ignore = true) // set in service layer
    Message toEntity(Dialogue dialogue, String text);

    @Mapping(target = "replyTo", source = "replyTo.uuid")
    MessageResponse toResponse(Message response);
}