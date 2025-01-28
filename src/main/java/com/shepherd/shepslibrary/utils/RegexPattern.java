package com.shepherd.shepslibrary.utils;

public final class RegexPattern {
    public static final String EMAIL = "^(?=.{1,254}$)(?!.*\\.\\.)(?!\\.)(?!.*\\.$)(?!.*@[^a-zA-Z0-9.-])(?!.*@.*\\.\\-)(?!.*@.*-\\.)[a-zA-Z0-9._%+-]{1,64}@[a-zA-Z0-9.-]{1,255}\\.[a-zA-Z]{2,}$";
    public static final String PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^\\w\\s]).{8,20}$";
    public static final String USER_NAME = "^[A-Za-z]+(?:[-'][A-Za-z]+)?$";

    public static final String BOOK_TITLE = "^[a-zA-Z0-9](?:[a-zA-Z0-9\\s.,'!?()-]{0,98}[a-zA-Z0-9])?$";
    public static final String BOOK_AUTHOR = "^[a-zA-Z](?:[a-zA-Z' -]{0,73}[a-zA-Z])?$";
    public static final String BOOK_GENRE = "^[a-zA-Z]+(?:[ -][a-zA-Z]+){0,19}$";
    public static final String ISBN = "^\\d{13}$";
    public static final String NUMBER = "^\\d+$";

    private RegexPattern() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
