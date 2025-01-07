package com.shepherd.shepslibrary.exceptions;


import lombok.Getter;
@Getter
public class PasswordValidationException extends ShepsLibraryException {
    private final int statusCode;

    public PasswordValidationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
