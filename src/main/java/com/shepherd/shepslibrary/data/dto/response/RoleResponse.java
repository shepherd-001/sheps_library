package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.UserRole;
import com.shepherd.shepslibrary.service.userRoleAndPermission.role.RoleNames;
import java.util.*;
import java.util.stream.Collectors;

public record RoleResponse(Long id, String name, long version, boolean systemRole, Set<String> permissionNames) {
    public static RoleResponse from(UserRole role) {
        Set<String> permissions = role.getPermissions().stream()
                .map(permission -> permission.getName()).collect(Collectors.toCollection(TreeSet::new));
        return new RoleResponse(role.getId(), role.getName(), role.getVersion(),
                RoleNames.SYSTEM_ROLES.contains(role.getName()), Collections.unmodifiableSet(permissions));
    }
}
