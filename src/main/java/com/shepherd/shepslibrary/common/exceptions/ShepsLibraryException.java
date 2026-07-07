package com.shepherd.shepslibrary.common.exceptions;

import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class ShepsLibraryException extends BaseApiException {
    public ShepsLibraryException(String message) {
        super(message, ErrorCode.SHEP_LIBRARY_ERROR, HttpStatus.BAD_REQUEST);
    }
}
