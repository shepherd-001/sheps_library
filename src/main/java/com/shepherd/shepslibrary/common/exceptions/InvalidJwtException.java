package com.shepherd.shepslibrary.common.exceptions;

import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidJwtException extends BaseApiException {
    public InvalidJwtException(String message) {
        super(message, ErrorCode.SHEP_LIBRARY_ERROR, HttpStatus.BAD_REQUEST);
    }
}
