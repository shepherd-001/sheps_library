package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.data.dto.request.AddRoleRequest;
import com.shepherd.shepslibrary.data.dto.request.AssignPermissionRequest;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;
import com.shepherd.shepslibrary.data.model.UserRole;


public interface AdminService {
    void createAdminIfNotExists();
    InviteLibrarianResponse inviteLibrarian(InviteLibrarianRequest request);
    String resendInvite(String inviteeEmail);
    String addRole(AddRoleRequest request);
    String deleteRole(String name);
    UserRole assignPermissionsToRole(AssignPermissionRequest request);
}
