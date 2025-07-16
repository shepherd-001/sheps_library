package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
import com.shepherd.shepslibrary.data.model.Role;

public interface UserService {
    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);
    UserResponse getUserById(String userId);
    PaginationResponse<UserResponse> getAllUsersByRole(Role role, int pageNumber);
    PaginationResponse<UserResponse> getAllUsersByStatus(boolean status, int pageNumber);
}
