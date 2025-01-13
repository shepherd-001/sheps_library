package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.data.dto.request.UserResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.model.Role;

import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID userId);
    PaginatedResponse<UserResponse> getAllUsersByRole(Role role, int pageNumber);
    PaginatedResponse<UserResponse> getAllUsersByStatus(boolean status, int pageNumber);
}
