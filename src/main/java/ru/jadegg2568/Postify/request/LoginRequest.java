package ru.jadegg2568.Postify.request;

import jakarta.validation.constraints.Size;
import ru.jadegg2568.Postify.exception.param.ParamCodes;
import ru.jadegg2568.Postify.exception.param.UserParamLimits;

public record LoginRequest(
        @Size(min = UserParamLimits.Min.LOGIN, max = UserParamLimits.Max.LOGIN,
                message = ParamCodes.INVALID_SIZE)
        String login,

        @Size(min = UserParamLimits.Min.PASSWORD, max = UserParamLimits.Max.PASSWORD,
                message = ParamCodes.INVALID_SIZE)
        String password
) {
}
