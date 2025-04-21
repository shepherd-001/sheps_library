package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.service.auth.AuthService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {
    private final AuthService authService;



    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verifyEmail(@RequestParam
                                                  @NotBlank(message = ValidationMessage.BLANK_TOKEN)
                                                  String token,
                                              @RequestParam
                                              @NotBlank(message = ValidationMessage.BLANK_EMAIL)
                                              @Email(regexp = RegexPattern.EMAIL, message = ValidationMessage.INVALID_EMAIL)
                                              String email){
        return ResponseEntity.ok(ApiResponse
                .buildResponse("User verified successfully", authService.verifyEmail(token, email)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(ApiResponse
                .buildResponse("User logged in successfully", authService.login(loginRequest)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(ApiResponse
                .buildResponse("Password changed successfully", authService.changePassword(changePasswordRequest)));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse<?>> requestPasswordReset(@RequestParam
                                                           @NotBlank(message = ValidationMessage.BLANK_EMAIL)
                                                           @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
                                                           String email) {
        return ResponseEntity.ok(ApiResponse
                .buildResponse(authService.requestPasswordReset(email)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(ApiResponse
                .buildResponse("Password reset successful", authService.resetPassword(resetPasswordRequest)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(){
        return ResponseEntity.ok(ApiResponse
                .buildResponse("User logged out successfully"));
    }
}
