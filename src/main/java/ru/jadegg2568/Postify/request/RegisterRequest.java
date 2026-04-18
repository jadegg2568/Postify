package ru.jadegg2568.Postify.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import ru.jadegg2568.Postify.exception.param.ParamCodes;
import ru.jadegg2568.Postify.exception.param.UserParamLimits;

public record RegisterRequest(
        @NotBlank(message = ParamCodes.EMPTY)
        @Email(message = ParamCodes.NOT_CORRECT)
        @Size(min = UserParamLimits.Min.MAIL, max = UserParamLimits.Max.MAIL,
                message = ParamCodes.INVALID_SIZE)
        String mail,

        @NotBlank(message = ParamCodes.EMPTY)
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!~`|{}\\[\\]();:'\",.<>/?\\\\_-]).*$",
                message = ParamCodes.INVALID_CHARACTERS
        )
        @Size(min = UserParamLimits.Min.PASSWORD, max = UserParamLimits.Max.PASSWORD,
                message = ParamCodes.INVALID_SIZE)
        String password,

        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = UserParamLimits.Min.NAME, max = UserParamLimits.Max.NAME,
                message = ParamCodes.INVALID_SIZE)
        String name,

        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = UserParamLimits.Min.DISPLAY_NAME, max = UserParamLimits.Max.DISPLAY_NAME,
                message = ParamCodes.INVALID_SIZE)
        String displayName,

        @Nullable
        @Size(min = UserParamLimits.Min.DESCRIPTION, max = UserParamLimits.Max.DESCRIPTION,
                message = ParamCodes.INVALID_SIZE)
        String description
) {
}
