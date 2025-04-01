package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.service.user.UserService;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody RegisterUserRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(registerRequest));
    }

    @GetMapping("/get/id")
    public ResponseEntity<Object> getUserById(@RequestParam @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                  String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/get/all-by-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAllUsersByRole(@RequestParam Role role,
                                                    @RequestParam(defaultValue = "0") int pageNumber) {
        return ResponseEntity.ok(userService.getAllUsersByRole(role, pageNumber));
    }

    @GetMapping("/get/all-by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAllUsersByStatus(@RequestParam boolean status,
                                                      @RequestParam(defaultValue = "0")
                                                      int pageNumber) {
        return ResponseEntity.ok(userService.getAllUsersByStatus(status, pageNumber));
    }
}
