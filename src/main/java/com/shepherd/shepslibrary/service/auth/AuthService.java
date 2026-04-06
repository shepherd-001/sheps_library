package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.VerifyEmailRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.dto.response.EmailVerificationResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
import com.shepherd.shepslibrary.security.AuthenticatedUser;

public interface AuthService {
    EmailVerificationResponse verifyEmail(VerifyEmailRequest request);
    AuthResponse login(LoginRequest loginRequest);
    UserResponse getAuthenticatedUser(AuthenticatedUser authenticatedUser);
    AuthResponse changePassword(ChangePasswordRequest changePasswordRequest, AuthenticatedUser authenticatedUser);
    String requestPasswordReset(String email);
    AuthResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
}
