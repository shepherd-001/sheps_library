package com.shepherd.shepslibrary.common.exceptions;

import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class ShepsTokenException extends BaseApiException {
    public ShepsTokenException(String message) {
        super(message, ErrorCode.SHEP_LIBRARY_ERROR, HttpStatus.BAD_REQUEST);
    }
}
