package com.shepherd.shepslibrary.service.userRoleAndPermission.permission;

import java.util.*;
import java.util.stream.Collectors;

/** Stable application capabilities. Changes require a reviewed catalogue migration. */
public enum PermissionCatalog {
    BOOK_CREATE("book:create", "Create books"),
    BOOK_READ("book:read", "Read the book catalogue"),
    BOOK_UPDATE("book:update", "Update book metadata"),
    BOOK_DELETE("book:delete", "Delete books"),
    LOAN_BORROW("loan:borrow", "Borrow a book"),
    LOAN_RETURN_OWN("loan:return:own", "Return own loans"),
    LOAN_READ_OWN("loan:read:own", "Read own loans"),
    LOAN_READ_ANY("loan:read:any", "Read any user's loans"),
    RESERVATION_CREATE("reservation:create", "Reserve a book"),
    RESERVATION_READ_OWN("reservation:read:own", "Read own reservations"),
    RESERVATION_READ_ANY("reservation:read:any", "Read any user's reservations"),
    RESERVATION_DELETE_OWN("reservation:delete:own", "Delete own reservations"),
    USER_READ("user:read", "Read user accounts"),
    LIBRARIAN_INVITE("librarian:invite", "Invite librarians"),
    ROLE_READ("role:read", "Read roles and the permission catalogue"),
    ROLE_CREATE("role:create", "Create custom roles"),
    ROLE_DELETE("role:delete", "Delete unassigned custom roles"),
    ROLE_PERMISSIONS_UPDATE("role:permissions:update", "Replace custom role permissions");

    private final String code;
    private final String description;

    PermissionCatalog(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }
    public String description() { return description; }
    public boolean administrative() { return code.startsWith("role:"); }

    private static final Map<String, PermissionCatalog> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(PermissionCatalog::code, permission -> permission));

    public static Set<String> codes() { return BY_CODE.keySet(); }

    public static PermissionCatalog fromCode(String code) {
        PermissionCatalog permission = BY_CODE.get(code);
        if (permission == null) throw new IllegalArgumentException("Unknown permission: " + code);
        return permission;
    }
}
