package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.*;
import com.shepherd.shepslibrary.data.dto.response.*;
import com.shepherd.shepslibrary.service.userRoleAndPermission.role.RoleService;
import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.PermissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
public class RoleController {
    private final RoleService roleService;
    private final PermissionService permissionService;

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> permissions() {
        return ResponseEntity.ok(ApiResponse.of(permissionService.catalogue()));
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse<?>> roles(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "name") String sort,
                                               @RequestParam(defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(ApiResponse.of(roleService.listRoles(PaginationRequest.of(page, size, sort, direction))));
    }

    @GetMapping("/roles/{name}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponse<RoleResponse>> role(@PathVariable String name) {
        return ResponseEntity.ok(ApiResponse.of(roleService.getRoleDetails(name)));
    }

    @PostMapping({"/roles", "/add-role"})
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody AddRoleRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.of("Role created", roleService.createRole(request)));
    }

    @PutMapping("/roles/permissions")
    @PreAuthorize("hasAuthority('role:permissions:update')")
    public ResponseEntity<ApiResponse<RoleResponse>> replace(@Valid @RequestBody AssignPermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.of("Role permissions replaced", roleService.replaceRolePermissions(request)));
    }

    /** Compatibility URL; semantics are explicitly full replacement and require version/reason. */
    @PutMapping("/assign-permission-to-role")
    @PreAuthorize("hasAuthority('role:permissions:update')")
    public ResponseEntity<ApiResponse<RoleResponse>> replaceLegacy(@Valid @RequestBody AssignPermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.of("Role permissions replaced", roleService.replaceRolePermissions(request)));
    }

    @DeleteMapping("/delete-role")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> delete(@RequestParam @NotBlank @Size(max = 50) String roleName,
                                       @RequestParam @PositiveOrZero Long expectedVersion,
                                       @RequestParam @NotBlank @Size(max = 500) String reason) {
        roleService.deleteRole(roleName, expectedVersion, reason);
        return ResponseEntity.noContent().build();
    }
}
