package com.shepherd.shepslibrary.utils;

public final class RegexPattern {
    public static final String EMAIL = "^\\s*(?=.{1,254}$)(?!.*\\.\\.)(?!.*@.*@)(?!.*[_%+\\-]{2,})([a-zA-Z0-9](?:[a-zA-Z0-9._%+\\-]*[a-zA-Z0-9])?)@([a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?)\\.([a-zA-Z]{2,24})\\s*$";
    public static final String PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*().?]).{8,20}$";
    public static final String USER_NAME = "^\\s*[A-Za-z]+(?:[-'][A-Za-z]+)?\\s*$";

    public static final String BOOK_TITLE = "^\\s*[a-zA-Z0-9](?:[a-zA-Z0-9\\s.,'!?()-]{0,98}[a-zA-Z0-9])?\\s*$";
    public static final String BOOK_AUTHOR = "^\\s*[a-zA-Z](?:[a-zA-Z' -]{0,73}[a-zA-Z])?\\s*$";
    public static final String BOOK_GENRE = "^\\s*[a-zA-Z]+(?:[ -][a-zA-Z]+){0,19}\\s*$";
    public static final String ISBN = "^\\d{13}$";
    public static final String ROLE = "^[a-zA-Z0-9]+$";


    private RegexPattern() {
        throw new UnsupportedOperationException(ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS);
    }
}
