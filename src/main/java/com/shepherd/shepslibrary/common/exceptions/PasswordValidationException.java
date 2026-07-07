package com.shepherd.shepslibrary.common.exceptions;


import com.shepherd.shepslibrary.common.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PasswordValidationException extends BaseApiException {
    private final int statusCode;

    public PasswordValidationException(String message, int statusCode) {
        super(message, ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        this.statusCode = statusCode;
    }
}
