package com.shepherd.shepslibrary.common.exceptions;

import com.shepherd.shepslibrary.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class MailSenderException extends BaseApiException {
    public MailSenderException(String message) {
        super(message, ErrorCode.EXTERNAL_SERVICE_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
