package ru.jadegg2568.Postify.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import ru.jadegg2568.Postify.exception.ParamCodes;
import ru.jadegg2568.Postify.user.UserParamLimits;

public record RegisterRequest(
        @NotBlank(message = ParamCodes.EMPTY)
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = ParamCodes.INVALID_CHARACTERS
        )
        @Size(min = UserParamLimits.Min.MAIL, max = UserParamLimits.Max.MAIL, message = ParamCodes.INVALID_SIZE)
        String mail,

        @NotBlank(message = ParamCodes.EMPTY)
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!~`|{}\\[\\]();:'\",.<>/?\\\\_-]).*$",
                message = ParamCodes.INVALID_CHARACTERS
        )
        @Size(min = UserParamLimits.Min.PASSWORD, max = UserParamLimits.Max.PASSWORD,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User password", example = "123456")
        String password,

        @NotBlank(message = ParamCodes.EMPTY)
        @Pattern(
                regexp = "^[A-Za-z0-9_]+$",
                message = ParamCodes.INVALID_CHARACTERS
        )
        @Size(min = UserParamLimits.Min.NAME, max = UserParamLimits.Max.NAME,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "Username", example = "username")
        String name,

        @NotBlank(message = ParamCodes.EMPTY)
        @Size(min = UserParamLimits.Min.DISPLAY_NAME, max = UserParamLimits.Max.DISPLAY_NAME,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User display name", example = "TestUser")
        String displayName,

        @Nullable
        @Size(min = UserParamLimits.Min.DESCRIPTION, max = UserParamLimits.Max.DESCRIPTION,
                message = ParamCodes.INVALID_SIZE)
        @Schema(description = "User description", example = "I'm testuser, hello!")
        String description
) {
}
