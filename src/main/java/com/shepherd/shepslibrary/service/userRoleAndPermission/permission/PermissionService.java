package com.shepherd.shepslibrary.service.userRoleAndPermission.permission;

import com.shepherd.shepslibrary.data.dto.response.PermissionResponse;
import com.shepherd.shepslibrary.data.model.Permission;
import java.util.List;
import java.util.Set;

public interface PermissionService {
    List<PermissionResponse> catalogue();
    Set<Permission> resolve(Set<String> codes);
}
