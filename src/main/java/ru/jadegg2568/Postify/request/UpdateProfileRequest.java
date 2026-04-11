package ru.jadegg2568.Postify.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import ru.jadegg2568.Postify.exception.param.ParamCodes;
import ru.jadegg2568.Postify.exception.param.UserParamLimits;

public record UpdateProfileRequest(
        @Nullable
        @Size(min = UserParamLimits.Min.NAME, max = UserParamLimits.Max.NAME,
                message = ParamCodes.INVALID_SIZE)
        String name,

        @Nullable
        @Size(min = UserParamLimits.Min.DISPLAY_NAME, max = UserParamLimits.Max.DISPLAY_NAME,
                message = ParamCodes.INVALID_SIZE)
        String displayName,

        @Nullable
        @Size(min = UserParamLimits.Min.DESCRIPTION, max = UserParamLimits.Max.DESCRIPTION,
                message = ParamCodes.INVALID_SIZE)
        String description
) {
}
