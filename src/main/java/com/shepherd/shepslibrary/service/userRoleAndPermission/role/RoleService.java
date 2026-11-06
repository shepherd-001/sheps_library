package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.request.*;
import com.shepherd.shepslibrary.data.dto.response.RoleResponse;
import com.shepherd.shepslibrary.data.model.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface RoleService {
    /** Internal account provisioning lookup; not an administrative API response. */
    UserRole getRole(String name);
    RoleResponse getRoleDetails(String name);
    PaginationResponse<RoleResponse> listRoles(PaginationRequest pagination);
    RoleResponse createRole(@NotNull @Valid AddRoleRequest request);
    RoleResponse replaceRolePermissions(@NotNull @Valid AssignPermissionRequest request);
    void deleteRole(String name, Long expectedVersion, String reason);
}
