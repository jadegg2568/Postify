package ru.jadegg2568.Postify.mapper;

import org.mapstruct.*;
import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.Post;
import ru.jadegg2568.Postify.entity.User;
import ru.jadegg2568.Postify.request.PostCreateRequest;
import ru.jadegg2568.Postify.request.PostUpdateRequest;
import ru.jadegg2568.Postify.response.DialogueResponse;
import ru.jadegg2568.Postify.response.PostResponse;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DialogueMapper {

    DialogueResponse toResponse(Dialogue dialogue);
}