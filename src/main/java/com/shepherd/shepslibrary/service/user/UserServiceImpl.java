package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.shepherd.shepslibrary.utils.AppUtils.NUMBER_OF_ITEMS_PER_PAGE;
import static com.shepherd.shepslibrary.utils.AppUtils.SORT_BY_CREATED_AT;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "userCache", key = "#userId")
    public BaseResponse<UserResponse> getUserById(UUID userId) {
        log.info("::::: Fetching a user by id :::::");
        return userRepository.findById(userId)
                .map(user -> BaseResponse.buildResponse(mapToUserResponse(user)))
                .orElseThrow(()-> new ResourceNotFoundException("User with the provided Id not found"));
    }

    @Override
    @Cacheable(value = "userCache", key = "'role:' + #role + ':page:' + #pageNumber")
    public BaseResponse<PaginatedResponse<UserResponse>> getAllUsersByRole(Role role, int pageNumber) {
        log.info("::::: Fetching all users by role :::::");
        Pageable pageable = findAllUsersPageRequest(pageNumber);
        Page<User> users = userRepository.findAllByRole(role, pageable);
        return BaseResponse.buildResponse(buildPaginatedUserResponse(users));
    }

    private Pageable findAllUsersPageRequest(int pageNumber){
        return AppUtils.createPageRequest(pageNumber, NUMBER_OF_ITEMS_PER_PAGE, SORT_BY_CREATED_AT, Sort.Direction.DESC);
    }

    private PaginatedResponse<UserResponse> buildPaginatedUserResponse(Page<User> users) {
        List<UserResponse> content = users.isEmpty() ? Collections.emptyList() :
                users.stream().map(this::mapToUserResponse).toList();

        return PaginatedResponse.<UserResponse>builder()
                .content(content)
                .numberOfElements(users.getNumberOfElements())
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .last(users.isLast())
                .build();
    }

    @Override
    @Cacheable(value = "userCache", key = "'status:' + #status + ':page:' + #pageNumber")
    public BaseResponse<PaginatedResponse<UserResponse>> getAllUsersByStatus(boolean status, int pageNumber) {
        log.info("::::: Fetching all users by status :::::");
        Pageable pageable = findAllUsersPageRequest(pageNumber);
        Page<User> users = userRepository.findAllByIsEnabled(status, pageable);
        return BaseResponse.buildResponse(buildPaginatedUserResponse(users));
    }

    UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .gender(user.getGender())
                .isEnabled(user.isEnabled())
                .isRevoked(user.isRevoked())
                .build();
    }
}