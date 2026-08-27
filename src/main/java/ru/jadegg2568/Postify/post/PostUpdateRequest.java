package ru.jadegg2568.Postify.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import ru.jadegg2568.Postify.common.exception.ParamCodes;

public record PostUpdateRequest(
        @Nullable
        @Size(min = PostParamLimits.Min.TITLE, max = PostParamLimits.Max.TITLE, message = ParamCodes.INVALID_SIZE)
        @Schema(description = "Post title", example = "Updated title")
        String title,

        @Nullable
        @Size(min = PostParamLimits.Min.CONTENT, max = PostParamLimits.Max.CONTENT, message = ParamCodes.INVALID_SIZE)
        @Schema(description = "Post content", example = "Updated content")
        String content
) {
}

