package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.PermissionCatalog;
import java.util.Map;
import java.util.Set;

/** Deployment-owned policy. Keep the versioned authorization data migration in sync. */
public final class SystemRolePolicy {
    private SystemRolePolicy() {}
    public static Map<String, Set<String>> defaults() {
        return Map.of(
                "ADMIN", PermissionCatalog.codes(),
                "LIBRARIAN", Set.of("book:read", "loan:read:any", "reservation:read:any"),
                "MEMBER", Set.of("book:read", "loan:borrow", "loan:return:own", "loan:read:own",
                        "reservation:create", "reservation:read:own", "reservation:delete:own"));
    }
}
