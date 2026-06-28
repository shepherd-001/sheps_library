package com.shepherd.shepslibrary.common.exceptions;


import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends BaseApiException {
    public AlreadyExistsException(String message) {
        super(message,
                ErrorCode.RESOURCE_ALREADY_EXISTS,
                HttpStatus.CONFLICT);
    }
}
