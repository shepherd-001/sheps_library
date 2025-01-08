package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.*;
import com.shepherd.shepslibrary.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody RegisterUserRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.buildResponse(authService.registerUser(registerRequest)));
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verifyEmail(@RequestParam String token){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(authService.verifyEmail(token)));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(BaseResponse
                .buildResponse(authService.login(loginRequest)));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(BaseResponse
                .buildResponse(authService.changePassword(changePasswordRequest)));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Object> requestPasswordReset(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        return ResponseEntity.ok(BaseResponse
                .buildResponse(authService.requestPasswordReset(passwordResetRequest)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(BaseResponse
                .buildResponse(authService.resetPassword(resetPasswordRequest)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(){
        return ResponseEntity.ok(BaseResponse
                .buildResponse("User logged out successfully"));
    }
}
