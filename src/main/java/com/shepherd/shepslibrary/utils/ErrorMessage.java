package com.shepherd.shepslibrary.utils;

public final class ErrorMessage {
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String NON_INSTANTIABLE_UTILITY_CLASS = "Utility class cannot be instantiated";
    public static final String RESERVATION_NOT_FOUND = "Reservation not found";
    public static final String ACCESS_DENIED = "Access denied";


    private ErrorMessage() {
        throw new UnsupportedOperationException(NON_INSTANTIABLE_UTILITY_CLASS);
    }
}
