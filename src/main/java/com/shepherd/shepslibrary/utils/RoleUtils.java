package com.shepherd.shepslibrary.utils;

public final class RoleUtils {
    public static final String ADMIN = "ADMIN";
    public static final String MEMBER = "MEMBER";
    public static final String LIBRARIAN = "LIBRARIAN";

    private RoleUtils() {
        throw new UnsupportedOperationException(ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS);
    }
}
