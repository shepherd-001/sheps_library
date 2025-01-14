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


    public static final String BLANK_TITLE = "Title is required";
    public static final String BLANK_AUTHOR = "Author's name is required";
    public static final String BLANK_GENRE = "Genre is required";

    public static final String INVALID_TITLE = "The title is invalid. Titles can only contain letters, numbers, spaces, and common punctuation";
    public static final String INVALID_AUTHOR = "The author's name is invalid. Names can only contain letters, spaces, apostrophes, or hyphens";
    public static final String INVALID_GENRE = "The genre is invalid. Genres can only contain letters, spaces, or hyphens";


    public static final String BLANK_ID = "Id is required";
    public static final String BLANK_PAGE_NUMBER = "Page number cannot be blank";
    private ValidationMessage() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
