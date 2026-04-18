package ru.jadegg2568.Postify.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.jadegg2568.Postify.exception.param.ParamCodes;
import ru.jadegg2568.Postify.exception.param.UserParamLimits;

public record LoginRequest(
        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = UserParamLimits.Min.LOGIN, max = UserParamLimits.Max.LOGIN,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User login", example = "username")
        String login,

        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = UserParamLimits.Min.PASSWORD, max = UserParamLimits.Max.PASSWORD,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User password", example = "123456")
        String password
) {
}
