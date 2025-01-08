package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.*;
import com.shepherd.shepslibrary.data.dto.response.*;

public interface AuthService {
    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);
    EmailConfirmationResponse verifyEmail(String token);
    LoginResponse login(LoginRequest loginRequest);
    ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest);
    RequestResetPasswordResponse requestPasswordReset(PasswordResetRequest passwordResetRequest);
    ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest);
}
