package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
import com.shepherd.shepslibrary.data.model.Role;

public interface UserService {
    BaseResponse<RegisterUserResponse> registerUser(RegisterUserRequest registerUserRequest);
    BaseResponse<UserResponse> getUserById(String userId);
    BaseResponse<PaginatedResponse<UserResponse>> getAllUsersByRole(Role role, int pageNumber);
    BaseResponse<PaginatedResponse<UserResponse>> getAllUsersByStatus(boolean status, int pageNumber);
}
