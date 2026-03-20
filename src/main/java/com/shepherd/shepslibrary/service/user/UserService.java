package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
//import com.shepherd.shepslibrary.data.model.Role;

public interface UserService {
    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);
    PaginationResponse<UserResponse> getAllUsersByRole(String roleName, PaginationRequest paginationRequest);
    PaginationResponse<UserResponse> getAllUsersByStatus(boolean status, PaginationRequest paginationRequest);
}
