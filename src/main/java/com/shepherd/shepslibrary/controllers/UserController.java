package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
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
                .success("User registered successfully", userService.registerUser(registerRequest)));
    }

    @GetMapping("/all/role")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllUsersByRole(@RequestParam String role,
                                                             @RequestParam(required = false, defaultValue = "1") int page,
                                                             @RequestParam(required = false, defaultValue = "10") int size,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(required = false) String direction){
        PaginationRequest paginationRequest = new PaginationRequest(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .success(userService.getAllUsersByRole(role, paginationRequest)));
    }

    @GetMapping("/all/status")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("hasAuthority('admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllUsersByStatus(@RequestParam boolean status,
                                                              @RequestParam(required = false, defaultValue = "1") int page,
                                                              @RequestParam(required = false, defaultValue = "10") int size,
                                                              @RequestParam(required = false) String sort,
                                                              @RequestParam(required = false) String direction){
        PaginationRequest paginationRequest = new PaginationRequest(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .success(userService.getAllUsersByStatus(status, paginationRequest)));
    }
}