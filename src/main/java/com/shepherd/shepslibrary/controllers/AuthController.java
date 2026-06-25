package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.VerifyEmailRequest;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.security.LogoutService;
import com.shepherd.shepslibrary.service.auth.AuthService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {
    private final AuthService authService;
    private final LogoutService logoutService;


    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(ApiResponse
                .success("Email verified successfully", authService.verifyEmail(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ApiResponse
                .success("Authentication successful", authService.login(loginRequest)));
    }

    @GetMapping("/user-detail")
    public ResponseEntity<ApiResponse<?>> getUserDetails(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(ApiResponse
                .success("User details retrieved successfully", authService.getAuthenticatedUser(authenticatedUser)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                         @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(ApiResponse
                .success("Password changed successfully", authService.changePassword(changePasswordRequest, authenticatedUser)));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse<?>> requestPasswordReset(@RequestParam
                                                               @NotBlank(message = ValidationMessage.BLANK_EMAIL)
                                                               @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
                                                               String email) {
        return ResponseEntity.ok(ApiResponse
                .success(authService.requestPasswordReset(email)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(ApiResponse
                .success("Password reset successful", authService.resetPassword(resetPasswordRequest)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logoutCurrentSession(HttpServletRequest request,
                                                               @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        logoutService.logoutCurrentSession(request, authenticatedUser.getUsername());
        return ResponseEntity.ok(ApiResponse
                .success("User logged out successfully"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<?>> logoutAllSessions(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        logoutService.logoutAllSessions(authenticatedUser.getUsername());
        return ResponseEntity.ok(ApiResponse
                .success("User logged out from all sessions"));
    }
}