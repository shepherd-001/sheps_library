package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/invite-librarian")
    public ResponseEntity<Object> inviteLibrarian(@Valid @RequestBody InviteLibrarianRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.buildResponse(adminService.inviteLibrarian(request)));
    }
}
