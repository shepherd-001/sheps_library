package com.shepherd.shepslibrary.utils;

public final class RegexPattern {
    public static final String EMAIL = "^(?!.*\\.\\.)(?!\\.)(?!.*\\.$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^\\w\\s]).{8,20}$";
    public static final String USER_NAME = "^[a-zA-Z]+(?:['-][a-zA-Z]+)$";

    public static final String BOOK_TITLE = "[a-zA-Z0-9][a-zA-Z0-9\\s.,'!?()-]*[a-zA-Z0-9]";
    public static final String BOOK_AUTHOR = "[a-zA-ZÀ-ÖØ-öø-ÿ]+([ '-][a-zA-ZÀ-ÖØ-öø-ÿ]+)";
    public static final String BOOK_GENRE = "[a-zA-Z]+([ -][a-zA-Z]+)";

    private RegexPattern() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
