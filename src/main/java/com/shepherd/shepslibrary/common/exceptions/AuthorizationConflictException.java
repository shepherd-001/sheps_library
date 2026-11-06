package com.shepherd.shepslibrary.common.exceptions;

import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class AuthorizationConflictException extends BaseApiException {
    public AuthorizationConflictException(String message) {
        super(message, ErrorCode.BUSINESS_RULE_VIOLATION, HttpStatus.CONFLICT);
    }
}
