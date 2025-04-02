package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;
import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.exceptions.UserAlreadyEnabledException;
import com.shepherd.shepslibrary.mapper.UserMapper;
import com.shepherd.shepslibrary.service.emailValidator.EmailValidationService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    @Value("${admin_email}")
    private String adminEmail;
    @Value("${admin_password}")
    private String adminPassword;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final MailNotificationService mailNotificationService;
    private final EmailValidationService emailValidationService;
    private final UserMapper userMapper;

    @PostConstruct
    private void createAdmin() {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("::::: Admin already exists :::::");
            return;
        }

        User admin = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .gender(Gender.MALE)
                .email(adminEmail.toLowerCase())
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .isEnabled(true)
                .isRevoked(false)
                .build();

        userRepository.save(admin);
        log.info("::::: Admin created successfully :::::");
    }


    @Override
    @Transactional
    public BaseResponse<InviteLibrarianResponse> inviteLibrarian(InviteLibrarianRequest request) {
        if(userRepository.existsByEmailEqualsIgnoreCase(request.getEmail().trim()))
            throw new AlreadyExistsException("User with the provided email already exists");
//        emailValidationService.checkAndValidateEmail(request.getEmail());

        User librarian = userRepository.save(userMapper.mapToLibrarian(request));

        sendLibrarianInvite(librarian);
        return BaseResponse.buildResponse("Librarian invited successfully",
                userMapper.mapToInviteLibrarianResponse(librarian));
    }

    private void sendLibrarianInvite(User user) {
        String token = tokenService.generateToken(user, TokenType.LIBRARIAN_INVITATION);
        mailNotificationService.sendLibrarianInvitation(user, token);
        log.info("::::: Librarian invited successfully to {} :::::", user.getEmail());
    }

    @Override
    @Transactional
    public BaseResponse<String> resendInvite(String inviteeEmail) {
        log.info("::::: Initiating resend invitation for email: {} :::::", inviteeEmail);
        return userRepository.findByEmailEqualsIgnoreCase(inviteeEmail.trim())
                .map(this::handleResendInvite)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("User not found. Invitation not resent"));
    }

    private BaseResponse<String> handleResendInvite(User user) {
        if(user.isEnabled()){
            log.error("::::: User with email {} is already enabled :::::", user.getEmail());
            throw new UserAlreadyEnabledException("User is already verified. Resend invitation not applicable");
        }
        if(!user.getRole().equals(Role.LIBRARIAN)){
            log.warn("::::: User does not have the role LIBRARIAN");
            throw new ShepsLibraryException("User is not a librarian. Resend invitation not applicable");
        }

        sendLibrarianInvite(user);
        return BaseResponse.buildResponse("Librarian invite has been resent successfully");
    }
}