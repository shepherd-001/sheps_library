package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/get/id")
    public ResponseEntity<Object> getUserById(@RequestParam UUID userId) {
        return ResponseEntity.ok(BaseResponse
                .buildResponse(userService.getUserById(userId)));
    }

    @GetMapping("/get/all-by-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAllUsersByRole(@RequestParam Role role,
                                                    @RequestParam(defaultValue = "0") int pageNumber) {
        return ResponseEntity.ok(BaseResponse
                .buildResponse(userService.getAllUsersByRole(role, pageNumber)));
    }

    @GetMapping("/get/all-by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAllUsersByStatus(@RequestParam boolean status,
                                                      @RequestParam(defaultValue = "0")
                                                      int pageNumber) {
        return ResponseEntity.ok(BaseResponse
                .buildResponse(userService.getAllUsersByStatus(status, pageNumber)));
    }
}
