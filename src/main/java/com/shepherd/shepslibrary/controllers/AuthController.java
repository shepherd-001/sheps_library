package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
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
    public ResponseEntity<Object> verifyEmail(@RequestParam
                                                  @NotBlank(message = ValidationMessage.BLANK_TOKEN)
                                                  String token,
                                              @RequestParam
                                              @NotBlank(message = ValidationMessage.BLANK_EMAIL)
                                              @Email(regexp = RegexPattern.EMAIL, message = ValidationMessage.INVALID_EMAIL)
                                              String email){
        return ResponseEntity.ok(authService.verifyEmail(token, email));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(authService.changePassword(changePasswordRequest));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Object> requestPasswordReset(@RequestParam
                                                           @NotBlank(message = ValidationMessage.BLANK_EMAIL)
                                                           @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
                                                           String email) {
        return ResponseEntity.ok(authService.requestPasswordReset(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(){
        return ResponseEntity.ok(BaseResponse
                .buildResponse("User logged out successfully"));
    }
}
