package com.shepherd.shepslibrary.common.exceptions;

import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserNotVerifiedException extends BaseApiException {
    public UserNotVerifiedException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR, HttpStatus.UNAUTHORIZED);
    }
}
