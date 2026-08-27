package ru.jadegg2568.Postify.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParamError {
    BUSY(ParamCodes.BUSY),
    INVALID_CHARACTERS(ParamCodes.INVALID_CHARACTERS),
    NOT_CORRECT(ParamCodes.NOT_CORRECT),
    TOO_LONG(ParamCodes.INVALID_SIZE),
    EMPTY(ParamCodes.EMPTY);

    private final String code;

    // Безопасный парсинг строки без генерации исключений
    public static ParamError fromMessage(String message) {
        if (message == null) return null;
        for (ParamError error : values()) {
            if (error.name().equals(message)) {
                return error;
            }
        }
        return null;
    }
}