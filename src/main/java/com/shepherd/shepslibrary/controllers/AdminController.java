package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/invite-librarian")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> inviteLibrarian(@Valid @RequestBody InviteLibrarianRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.buildResponse(adminService.inviteLibrarian(request)));
    }

    @PostMapping("/resend-librarian-invite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> resendLibrarian(@RequestParam String inviteeEmail){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(adminService.resendInvite(inviteeEmail)));
    }
}
