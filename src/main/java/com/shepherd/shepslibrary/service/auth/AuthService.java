package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.dto.response.EmailConfirmationResponse;

public interface AuthService {
    BaseResponse<EmailConfirmationResponse> verifyEmail(String token, String email);
    BaseResponse<AuthResponse> login(LoginRequest loginRequest);
    BaseResponse<AuthResponse> changePassword(ChangePasswordRequest changePasswordRequest);
    BaseResponse<String> requestPasswordReset(String email);
    BaseResponse<AuthResponse> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
