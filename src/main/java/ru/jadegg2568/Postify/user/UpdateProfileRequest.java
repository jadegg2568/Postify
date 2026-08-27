package ru.jadegg2568.Postify.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import ru.jadegg2568.Postify.exception.ParamCodes;

public record UpdateProfileRequest(
        @Nullable
        @Size(min = UserParamLimits.Min.NAME, max = UserParamLimits.Max.NAME,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User description", example = "I'm testuser, hello!")
        String name,

        @Nullable
        @Size(min = UserParamLimits.Min.DISPLAY_NAME, max = UserParamLimits.Max.DISPLAY_NAME,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User description", example = "I'm testuser, hello!")
        String displayName,

        @Nullable
        @Size(min = UserParamLimits.Min.DESCRIPTION, max = UserParamLimits.Max.DESCRIPTION,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User description", example = "I'm testuser, hello!")
        String description
) {
}
