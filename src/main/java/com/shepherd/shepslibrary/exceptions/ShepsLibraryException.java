package com.shepherd.shepslibrary.exceptions;

public class ShepsLibraryException extends RuntimeException {
    public ShepsLibraryException(String message) {
        super(message);
    }

    public ShepsLibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
