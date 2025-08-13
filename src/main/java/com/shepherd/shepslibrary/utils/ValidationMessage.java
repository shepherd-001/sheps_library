package com.shepherd.shepslibrary.utils;

public final class ValidationMessage {
    public static final String BLANK_FIRST_NAME = "First name is required";
    public static final String BLANK_LAST_NAME = "Last name is required";
    public static final String BLANK_EMAIL = "Email address is required";
    public static final String BLANK_PASSWORD = "Password is required";
    public static final String BLANK_TOKEN = "Token is required";
    public static final String BLANK_GENDER = "Gender is required";

//    public static final String INVALID_FIRST_NAME = "First name can only contain letters, apostrophes, and hyphens. It cannot start or end with apostrophes or hyphens";
//    public static final String INVALID_LAST_NAME = "Last name can only contain letters, apostrophes, and hyphens. It cannot start or end with apostrophes or hyphens";
    public static final String INVALID_FIRST_NAME = "Invalid first name";
    public static final String INVALID_LAST_NAME = "Invalid last name";
    public static final String INVALID_PASSWORD = "Password must be 8–16 characters with at least one uppercase, one lowercase, one digit, and one special character (e.g. !@#$%^&*().?)";
    public static final String INVALID_EMAIL = "Invalid email address";
    public static final String INVALID_GENDER = "Invalid gender. Allowed values: MALE, FEMALE";

    public static final String FIRST_NAME_TOO_LONG = "First name is too long";
    public static final String LAST_NAME_TOO_LONG = "Last name is too long";


    public static final String BLANK_TITLE = "Title is required";
    public static final String BLANK_AUTHOR = "Author's name is required";
    public static final String BLANK_GENRE = "Genre is required";
    public static final String BLANK_ISBN = "Book ISBN is required";


    public static final String INVALID_TITLE = "The title is invalid. Titles can only contain letters, numbers, spaces, and common punctuation";
    public static final String INVALID_AUTHOR = "The author's name is invalid. Names can only contain letters, spaces, apostrophes, or hyphens";
    public static final String INVALID_GENRE = "The genre is invalid. Genres can only contain letters, spaces, or hyphens";
    public static final String INVALID_ISBN = "Book ISBN must be exactly 13 digits in length";


    public static final String BLANK_BOOK_ID = "Book id is required";
    public static final String NULL_RESERVATION_ID = "Reservation id is required";
    public static final String NULL_USER_ID = "User id is required";
    public static final String NULL_USER_ROLE = "User role is required";
    public static final String NULL_USER_STATUS = "User status is required";
    public static final String NULL_TRANSACTION_ID = "Transaction id is required";


    public static final String NULL_PAGE_NUMBER = "Page number is required";
    public static final String INVALID_PAGE_NUMBER = "Page number must be a positive integer";


    public static final String BLANK_ROLE = "Role is required";
    public static final String INVALID_ROLE = "Role is invalid";
    public static final String ROLE_NAME_TOO_LONG = "Role name is too long";
    public static final String ROLE_DESCRIPTION_TOO_LONG = "Role description is too long";


    private ValidationMessage() {
        throw new UnsupportedOperationException(ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS);
    }
}
