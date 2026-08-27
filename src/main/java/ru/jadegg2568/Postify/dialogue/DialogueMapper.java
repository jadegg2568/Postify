package ru.jadegg2568.Postify.dialogue;

import org.mapstruct.*;
import ru.jadegg2568.Postify.user.UserMapper;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DialogueMapper {

    DialogueResponse toResponse(Dialogue dialogue);
}