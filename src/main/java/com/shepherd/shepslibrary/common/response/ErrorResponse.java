package com.shepherd.shepslibrary.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shepherd.shepslibrary.common.ErrorCode;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String message,
        ErrorCode code,
        Map<String, String> errors, // {field: message}
        Instant timeStamp,
        String path,
        boolean success
) {
    public static ErrorResponse of(
            String message,
            ErrorCode code,
            Map<String, String> errors,
            String path
    ) {
        return new ErrorResponse(message, code, errors, Instant.now(), path, false);
    }

    public static ErrorResponse of(
            String message,
            ErrorCode code,
            String path
    ) {
        return of(message, code, null, path);
    }

    public static ErrorResponse validationError(
            Map<String, String> errors,
            String path
    ) {
        return of("Validation failed", ErrorCode.VALIDATION_ERROR, errors, path);
    }

    public static ErrorResponse validationError(
            String message,
            String path
    ) {
        return of(message, null, null, path);
    }

    public static ErrorResponse internalServerError(
            String message,
            String path
    ) {
        return of(message, ErrorCode.INTERNAL_SERVER_ERROR, null, path);
    }

    public static ErrorResponse unauthorized(
            String message,
            String path
    ) {
        return ErrorResponse.of(message, ErrorCode.UNAUTHORIZED, null, path);
    }

    public static ErrorResponse forbidden(
            String message,
            String path
    ) {
        return of(message, ErrorCode.FORBIDDEN, null, path);
    }
}
