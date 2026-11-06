package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import java.util.Locale;
import java.util.Set;

public final class RoleNames {
    public static final Set<String> SYSTEM_ROLES = Set.of("ADMIN", "LIBRARIAN", "MEMBER");
    private RoleNames() {}

    public static String normalize(String name) {
        if (name == null) throw new IllegalArgumentException("Role name is required");
        String normalized = name.trim().toUpperCase(Locale.ROOT);
        if (!normalized.matches("[A-Z][A-Z0-9_]{0,49}")) {
            throw new IllegalArgumentException("Role name must contain 1-50 letters, digits or underscores and start with a letter");
        }
        return normalized;
    }
}
