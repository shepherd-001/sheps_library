package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.AddRoleRequest;
import com.shepherd.shepslibrary.data.dto.request.AssignPermissionRequest;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.service.admin.AdminService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/invite-librarian")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.create')")
    public ResponseEntity<ApiResponse<?>> inviteLibrarian(@Valid @RequestBody InviteLibrarianRequest inviteLibrarianRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .of("Librarian invited successfully", adminService.inviteLibrarian(inviteLibrarianRequest)));
    }

    @PostMapping("/resend-librarian-invite")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.create')")
    public ResponseEntity<ApiResponse<?>> resendLibrarianInvitation(@RequestParam
                                                      @NotBlank(message = ValidationMessage.BLANK_EMAIL)
                                                      @Pattern(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
                                                      String inviteeEmail){
        return ResponseEntity.ok(ApiResponse.of(adminService.resendInvite(inviteeEmail)));
    }

    @PostMapping("/add-role")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.create')")
    public ResponseEntity<ApiResponse<?>> addRole(@Valid @RequestBody AddRoleRequest addRoleRequest){
        return ResponseEntity.ok(ApiResponse
                .of(adminService.addRole(addRoleRequest)));
    }

    @PutMapping("/assign-permission-to-role")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.update')")
    public ResponseEntity<ApiResponse<?>> assignPermissionsToRole(@Valid @RequestBody AssignPermissionRequest assignPermissionRequest){
        return ResponseEntity.ok(ApiResponse
                .of(adminService.assignPermissionsToRole(assignPermissionRequest)));
    }

    @DeleteMapping("/delete-role")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.create')")
    public ResponseEntity<ApiResponse<?>> deleteRole(@RequestParam String roleName){
        return ResponseEntity.ok(ApiResponse
                .of(adminService.deleteRole(roleName)));
    }
}