package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.EmailConfirmationResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;

public interface AuthService {
    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);
    EmailConfirmationResponse verifyEmail(String token);

}
