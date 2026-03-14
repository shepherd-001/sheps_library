package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.model.UserRole;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.mapper.UserMapper;
import com.shepherd.shepslibrary.service.emailValidator.EmailValidationService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.service.passwordServie.PasswordValidationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import com.shepherd.shepslibrary.service.userRoleAndPermission.role.RoleService;
import com.shepherd.shepslibrary.utils.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.shepherd.shepslibrary.utils.AppUtils.*;
import static com.shepherd.shepslibrary.utils.RoleUtils.MEMBER;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final MailNotificationService mailNotificationService;
//    private final EmailValidationService emailValidationService;
//    private final PasswordValidationService passwordValidationService;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Override
    @Transactional
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        validateRegisterRequest(registerUserRequest);

        UserRole role = roleService.getRole(MEMBER);

        User user = userMapper.mapToUser(registerUserRequest);
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setRole(role);

        user = userRepository.save(user);
        sendEmailConfirmation(user);

        return userMapper.mapToRegisterResponse(user);
    }

    private void validateRegisterRequest(RegisterUserRequest registerUserRequest) {
        if(userRepository.existsByEmailEqualsIgnoreCase(registerUserRequest.getEmail().trim()))
            throw new AlreadyExistsException("User with the provided email already exists");
//        emailValidationService.checkAndValidateEmail(registerUserRequest.getEmail());
//        passwordValidationService.validatePasswordNotBreached(registerUserRequest.getPassword());
    }

    private void sendEmailConfirmation(User user) {
        mailNotificationService.sendVerificationMail(user,
                tokenService.generateToken(user, TokenType.EMAIL_CONFIRMATION));
    }

    @Override
    @Cacheable(value = "userCache", key = "#userId")
    public UserResponse getUserById(String userId) {
        log.info("::::: Fetching a user by id :::::");
        return userRepository.findById(userId)
                .map(userMapper::mapToUserResponse)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Cacheable(value = "userCache", key = "'role:' + #role + ':page:' + #pageNumber")
    public PaginationResponse<UserResponse> getAllUsersByRole(String role, int pageNumber) {
        log.info("::::: Fetching all users by role {} :::::", role);
        Pageable pageable = AppUtils.createPageRequest(pageNumber, DEFAULT_PAGE_SIZE, SORT_BY_CREATED_AT, SORT_DIRECTION_ASC);
        Page<User> users = userRepository.findAllByRoleName(role, pageable);
        return mapToPaginatedUserResponse(users);
    }

    private PaginationResponse<UserResponse> mapToPaginatedUserResponse(Page<User> users) {
        List<UserResponse> content = users.isEmpty() ? Collections.emptyList() :
                users.stream().map(userMapper::mapToUserResponse).toList();

        return PaginationResponse.<UserResponse>builder()
                .content(content)
                .page(users.getNumber())
                .size(users.getSize())
                .numberOfElements(users.getNumberOfElements())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .last(users.isLast())
                .build();
    }

    @Override
    @Cacheable(value = "userCache", key = "'status:' + #status + ':page:' + #pageNumber")
    public PaginationResponse<UserResponse> getAllUsersByStatus(boolean status, int pageNumber) {
        log.info("::::: Fetching all users by status {} :::::", status);
        Pageable pageable = AppUtils.createPageRequest(pageNumber, DEFAULT_PAGE_SIZE, SORT_BY_CREATED_AT, SORT_DIRECTION_ASC);
        Page<User> users = userRepository.findAllByEnabled(status, pageable);
        return mapToPaginatedUserResponse(users);
    }
}