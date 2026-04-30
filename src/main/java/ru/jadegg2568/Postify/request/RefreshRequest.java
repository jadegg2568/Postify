package ru.jadegg2568.Postify.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.jadegg2568.Postify.exception.param.ParamCodes;
import ru.jadegg2568.Postify.exception.param.UserParamLimits;

public record RefreshRequest(
        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = UserParamLimits.Min.TOKEN, max = UserParamLimits.Max.TOKEN,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5.riqerjRijer.reReiorERJrj")
        String refreshToken
) {
}
