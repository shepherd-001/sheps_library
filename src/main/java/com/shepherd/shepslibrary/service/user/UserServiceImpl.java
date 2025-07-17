package com.shepherd.shepslibrary.service.user;

import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;
import com.shepherd.shepslibrary.data.dto.response.UserResponse;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.mapper.UserMapper;
import com.shepherd.shepslibrary.service.emailValidator.EmailValidationService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.service.passwordServie.PasswordValidationService;
import com.shepherd.shepslibrary.service.token.TokenService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final MailNotificationService mailNotificationService;
    private final EmailValidationService emailValidationService;
    private final PasswordValidationService passwordValidationService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        validateRegisterRequest(registerUserRequest);

        User savedUser = userRepository.save(userMapper.mapToUser(registerUserRequest, passwordEncoder));
        sendEmailConfirmation(savedUser);

        log.info("::::: User with the first name {} registered successfully :::::", savedUser.getFirstName());
        return userMapper.mapToRegisterResponse(savedUser);
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
                .orElseThrow(()-> new ResourceNotFoundException("User with the provided Id not found"));
    }

    @Override
    @Cacheable(value = "userCache", key = "'role:' + #role + ':page:' + #pageNumber")
    public PaginationResponse<UserResponse> getAllUsersByRole(Role role, int pageNumber) {
        log.info("::::: Fetching all users by role {} :::::", role);
        Pageable pageable = AppUtils.createPageRequest(pageNumber, DEFAULT_PAGE_SIZE, SORT_BY_CREATED_AT, SORT_DIRECTION_ASC);
        Page<User> users = userRepository.findAllByRole(role, pageable);
        return mapToPaginatedUserResponse(users);
    }

    private PaginationResponse<UserResponse> mapToPaginatedUserResponse(Page<User> users) {
        List<UserResponse> content = users.isEmpty() ? Collections.emptyList() :
                users.stream().map(userMapper::mapToUserResponse).toList();

        return PaginationResponse.<UserResponse>builder()
                .content(content)
                .numberOfElements(users.getNumberOfElements())
                .totalPages(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .isLast(users.isLast())
                .build();
    }

    @Override
    @Cacheable(value = "userCache", key = "'status:' + #status + ':page:' + #pageNumber")
    public PaginationResponse<UserResponse> getAllUsersByStatus(boolean status, int pageNumber) {
        log.info("::::: Fetching all users by status {} :::::", status);
        Pageable pageable = AppUtils.createPageRequest(pageNumber, DEFAULT_PAGE_SIZE, SORT_BY_CREATED_AT, SORT_DIRECTION_ASC);
        Page<User> users = userRepository.findAllByIsEnabled(status, pageable);
        return mapToPaginatedUserResponse(users);
    }
}