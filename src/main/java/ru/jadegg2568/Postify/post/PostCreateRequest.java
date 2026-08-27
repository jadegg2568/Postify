package ru.jadegg2568.Postify.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.jadegg2568.Postify.common.exception.ParamCodes;

public record PostCreateRequest(
        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = PostParamLimits.Min.TITLE, max = PostParamLimits.Max.TITLE, message = ParamCodes.INVALID_SIZE)
        @Schema(description = "Post title", example = "Hello world")
        String title,

        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = PostParamLimits.Min.CONTENT, max = PostParamLimits.Max.CONTENT, message = ParamCodes.INVALID_SIZE)
        @Schema(description = "Post content", example = "My first post content")
        String content
) {
}

