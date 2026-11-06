package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.common.exceptions.*;
import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.request.*;
import com.shepherd.shepslibrary.data.dto.response.RoleResponse;
import com.shepherd.shepslibrary.data.model.*;
import com.shepherd.shepslibrary.data.repository.*;
import com.shepherd.shepslibrary.service.userRoleAndPermission.audit.*;
import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final UserRoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final AuthorizationPolicy policy;
    private final AuthorizationAuditRepository auditRepository;

    @Override
    public UserRole getRole(String name) { return find(RoleNames.normalize(name)); }

    @Override
    public RoleResponse getRoleDetails(String name) {
        policy.require("role:read");
        return RoleResponse.from(find(RoleNames.normalize(name)));
    }

    @Override
    public PaginationResponse<RoleResponse> listRoles(PaginationRequest pagination) {
        policy.require("role:read");
        var page = roleRepository.findAll(pagination.toPageable(Set.of("id", "name")));
        if (page.isEmpty()) return PaginationResponse.map(page, RoleResponse::from);
        Map<Long, RoleResponse> snapshots = roleRepository.findByIdIn(page.map(UserRole::getId).getContent()).stream()
                .collect(Collectors.toMap(UserRole::getId, RoleResponse::from));
        return PaginationResponse.map(page, role -> snapshots.get(role.getId()));
    }

    @Override
    @Transactional
    public RoleResponse createRole(AddRoleRequest request) {
        AuthorizationPolicy.Actor actor = policy.require("role:create");
        String name = RoleNames.normalize(request.name());
        policy.checkTarget(actor, name);
        String reason = reason(request.reason());
        if (roleRepository.existsByNameEqualsIgnoreCase(name)) throw new AlreadyExistsException("Role already exists");
        UserRole role = roleRepository.saveAndFlush(UserRole.builder().name(name).build());
        auditRepository.save(new AuthorizationAudit(actor.userId(), "ROLE_CREATED", role.getId(), name,
                null, role.getVersion(), Set.of(), Set.of(), reason));
        return RoleResponse.from(role);
    }

    @Override
    @Transactional
    public RoleResponse replaceRolePermissions(AssignPermissionRequest request) {
        AuthorizationPolicy.Actor actor = policy.require("role:permissions:update");
        String name = RoleNames.normalize(request.roleName());
        policy.checkTarget(actor, name);
        String reason = reason(request.reason());
        UserRole role = find(name);
        checkVersion(role, request.expectedVersion());
        Set<String> codes = normalizePermissions(request.permissionNames());
        policy.checkGrant(actor, codes);
        Set<String> before = permissionNames(role);
        policy.checkGrant(actor, before);
        if (before.equals(codes)) return RoleResponse.from(role);
        Set<Permission> permissions = permissionService.resolve(codes);
        long previousVersion = role.getVersion();
        role.setPermissions(new HashSet<>(permissions));
        // Flush the owning association so Hibernate checks/increments @Version before the response/audit snapshot.
        roleRepository.flush();
        auditRepository.save(new AuthorizationAudit(actor.userId(), "ROLE_PERMISSIONS_REPLACED", role.getId(), name,
                previousVersion, role.getVersion(), before, codes, reason));
        return RoleResponse.from(role);
    }

    @Override
    @Transactional
    public void deleteRole(String rawName, Long expectedVersion, String rawReason) {
        AuthorizationPolicy.Actor actor = policy.require("role:delete");
        String name = RoleNames.normalize(rawName);
        policy.checkTarget(actor, name);
        String reason = reason(rawReason);
        UserRole role = find(name);
        checkVersion(role, expectedVersion);
        if (userRepository.existsByRoleId(role.getId())) {
            throw new AuthorizationConflictException("Reassign users before deleting this role");
        }
        Set<String> before = permissionNames(role);
        // Prevent delegated administrators from deleting a role above their delegation boundary.
        policy.checkGrant(actor, before);
        auditRepository.save(new AuthorizationAudit(actor.userId(), "ROLE_DELETED", role.getId(), name,
                role.getVersion(), null, before, Set.of(), reason));
        roleRepository.delete(role);
        roleRepository.flush();
    }

    private UserRole find(String name) {
        return roleRepository.findByNameEqualsIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    private void checkVersion(UserRole role, Long expected) {
        if (expected == null || expected < 0) throw new IllegalArgumentException("A non-negative expectedVersion is required");
        if (role.getVersion() != expected) throw new AuthorizationConflictException("Role changed; reload it before retrying");
    }

    private Set<String> normalizePermissions(List<String> names) {
        if (names == null || names.size() > 100) throw new IllegalArgumentException("Provide at most 100 permission names");
        Set<String> normalized = new HashSet<>();
        for (String name : names) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Permission names must not be blank");
            String code = name.trim().toLowerCase(Locale.ROOT);
            PermissionCatalog.fromCode(code);
            if (!normalized.add(code)) throw new IllegalArgumentException("Duplicate permission: " + code);
        }
        return normalized;
    }

    private String reason(String reason) {
        if (reason == null || reason.isBlank() || reason.length() > 500) {
            throw new IllegalArgumentException("A change reason of 1-500 characters is required");
        }
        return reason.trim();
    }

    private Set<String> permissionNames(UserRole role) {
        return role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet());
    }
}
