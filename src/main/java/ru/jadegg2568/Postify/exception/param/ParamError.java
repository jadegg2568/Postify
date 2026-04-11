package ru.jadegg2568.Postify.exception.param;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParamError {
    BUSY(ParamCodes.BUSY),
    INVALID_CHARACTERS(ParamCodes.INVALID_CHARACTERS),
    NOT_CORRECT(ParamCodes.NOT_CORRECT),
    TOO_LONG(ParamCodes.INVALID_SIZE);

    private final String code;

}
