package com.shepherd.shepslibrary.utils;

public final class ValidationMessage {
    public static final String BLANK_FIRST_NAME = "First name is required";
    public static final String BLANK_LAST_NAME = "Last name is required";
    public static final String BLANK_EMAIL = "Email address is required";
    public static final String BLANK_PASSWORD = "Password is required";
    public static final String BLANK_GENDER = "Gender is required";
    public static final String BLANK_ROLE = "Role is required";
    public static final String BLANK_TOKEN = "Token is required";

    public static final String INVALID_FIRST_NAME = "First name can only contain letters, apostrophes, and hyphens. It cannot start or end with apostrophes or hyphens";
    public static final String INVALID_LAST_NAME = "Last name can only contain letters, apostrophes, and hyphens. It cannot start or end with apostrophes or hyphens";
    public static final String INVALID_PASSWORD = "Password must be between 8 and 20 characters long and include at least one lowercase letter," +
            " one uppercase letter, one number, and one special character (e.g., @, #, $, %, ^, &, +, =, !, ...)";
    public static final String INVALID_EMAIL = "Invalid email address";

    private ValidationMessage() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
