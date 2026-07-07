package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.data.model.UserRole;

import java.util.List;

public interface RoleService {
    UserRole getRole(String name);
    UserRole addRole(String name);
    void deleteRole(String name);
    UserRole assignPermissionsToRole(String name, List<String> permissionNames);
    void updateCache(UserRole role);
}
