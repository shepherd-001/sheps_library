package com.shepherd.shepslibrary.service.userRoleAndPermission.permission;

import com.shepherd.shepslibrary.data.model.Permission;

public interface PermissionService {
    Permission getPermission(String name);
    Permission addPermission(String name);
    void deletePermission(String name);
}
