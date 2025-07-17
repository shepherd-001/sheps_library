package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.service.user.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody RegisterUserRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .buildResponse("User registered successfully", userService.registerUser(registerRequest)));
    }
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse
                .buildResponse(userService.getUserById(userId)));
    }

    @GetMapping("/all/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllUsersByRole(@RequestParam Role role, int pageNumber) {
        return ResponseEntity.ok(ApiResponse
                .buildResponse(userService.getAllUsersByRole(role, pageNumber)));
    }

    @GetMapping("/all/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getAllUsersByStatus(@RequestParam boolean status, int pageNumber) {
        return ResponseEntity.ok(ApiResponse
                .buildResponse(userService.getAllUsersByStatus(status, pageNumber)));
    }
}
