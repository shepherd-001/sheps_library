package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.*;

public interface AuthService {
    BaseResponse<RegisterUserResponse> registerUser(RegisterUserRequest registerUserRequest);
    BaseResponse<EmailConfirmationResponse> verifyEmail(String token, String email);
    BaseResponse<AuthResponse> login(LoginRequest loginRequest);
    BaseResponse<ChangePasswordResponse> changePassword(ChangePasswordRequest changePasswordRequest);
    BaseResponse<String> requestPasswordReset(String email);
    BaseResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
