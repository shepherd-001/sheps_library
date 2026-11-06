package com.shepherd.shepslibrary.service.userRoleAndPermission.permission;

import com.shepherd.shepslibrary.data.dto.response.PermissionResponse;
import com.shepherd.shepslibrary.data.model.Permission;
import com.shepherd.shepslibrary.data.repository.PermissionRepository;
import com.shepherd.shepslibrary.service.userRoleAndPermission.role.AuthorizationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final AuthorizationPolicy policy;

    @Override
    public List<PermissionResponse> catalogue() {
        policy.require("role:read");
        return Arrays.stream(PermissionCatalog.values())
                .map(permission -> new PermissionResponse(permission.code(), permission.description())).toList();
    }

    @Override
    public Set<Permission> resolve(Set<String> codes) {
        policy.require("role:permissions:update");
        codes.forEach(PermissionCatalog::fromCode);
        if (codes.isEmpty()) return Set.of();
        Set<Permission> permissions = permissionRepository.findByNameIn(codes);
        Set<String> found = permissions.stream().map(Permission::getName).collect(Collectors.toSet());
        if (!found.equals(codes)) {
            throw new IllegalStateException("Permission catalogue database is out of date; apply the authorization migration");
        }
        return permissions;
    }
}
