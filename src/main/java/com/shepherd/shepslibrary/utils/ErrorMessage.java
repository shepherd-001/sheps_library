package com.shepherd.shepslibrary.utils;

public final class ErrorMessage {
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String NON_INSTANTIABLE_UTILITY_CLASS = "Utility class cannot be instantiated";
    public static final String RESERVATION_NOT_FOUND = "Reservation not found";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String ACCOUNT_DISABLED = "You account is currently disable. Please contact your admin or verify your email address";
    public static final String INVALID_CURRENT_PASSWORD =  "Invalid current password";
    public static final String SAME_OLD_AND_NEW_PASSWORD =  "New password cannot be same as the current";
    public static final String MISMATCH_PASSWORD = "Passwords do not match";
    public static final String USER_WITH_EMAIL_NOT_FOUND = "User with the provided email not found";



    private ErrorMessage() {
        throw new UnsupportedOperationException(NON_INSTANTIABLE_UTILITY_CLASS);
    }
}
