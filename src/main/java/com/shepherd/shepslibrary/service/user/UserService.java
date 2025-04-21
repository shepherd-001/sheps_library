package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
import com.shepherd.shepslibrary.data.model.Role;

import java.util.UUID;

public interface UserService {
    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);
    UserResponse getUserById(UUID userId);
    PaginatedResponse<UserResponse> getAllUsersByRole(Role role, int pageNumber);
    PaginatedResponse<UserResponse> getAllUsersByStatus(boolean status, int pageNumber);
}
