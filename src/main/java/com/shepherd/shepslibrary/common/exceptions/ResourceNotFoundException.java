package com.shepherd.shepslibrary.common.exceptions;

import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseApiException {
    public ResourceNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
