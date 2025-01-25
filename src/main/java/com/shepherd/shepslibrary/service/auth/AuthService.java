package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.*;

public interface AuthService {
    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);
    EmailConfirmationResponse verifyEmail(String token);
    LoginResponse login(LoginRequest loginRequest);
    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest);
    RequestResetPasswordResponse requestPasswordReset(String email);
    ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
}
