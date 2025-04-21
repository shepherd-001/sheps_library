package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.dto.response.EmailConfirmationResponse;

public interface AuthService {
    EmailConfirmationResponse verifyEmail(String token, String email);
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse changePassword(ChangePasswordRequest changePasswordRequest);
    String requestPasswordReset(String email);
    AuthResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
}
