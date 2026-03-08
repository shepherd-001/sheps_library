package com.shepherd.shepslibrary.exceptions;

public class ShepsLibraryException extends RuntimeException {
    public ShepsLibraryException(String message) {
        super(message);
    }

    public ShepsLibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
// public abstract class BaseApiException extends RuntimeException {
//
//    private final HttpStatus status;
//
//    protected BaseApiException(String message, HttpStatus status) {
//        super(message);
//        this.status = status;
//    }
//
//    public HttpStatus getStatus() {
//        return status;
//    }
//}