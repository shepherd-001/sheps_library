package com.shepherd.shepslibrary.common.exceptions;


import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmailValidationException extends BaseApiException {

    public EmailValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
    }
}
