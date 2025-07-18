package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.service.admin.AdminService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> inviteLibrarian(@Valid @RequestBody InviteLibrarianRequest inviteLibrarianRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .buildResponse("Librarian invited successfully", adminService.inviteLibrarian(inviteLibrarianRequest)));
    }

    @PostMapping("/resend-librarian-invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> resendLibrarianInvitation(@RequestParam
                                                      @NotBlank(message = ValidationMessage.BLANK_EMAIL)
                                                      @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
                                                      String inviteeEmail){
        return ResponseEntity.ok(ApiResponse.buildResponse(adminService.resendInvite(inviteeEmail)));
    }
}
