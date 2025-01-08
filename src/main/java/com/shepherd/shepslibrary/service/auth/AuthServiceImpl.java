package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.EmailConfirmationResponse;
import com.shepherd.shepslibrary.data.dto.response.JwtTokenResponse;
import com.shepherd.shepslibrary.data.dto.response.RegisterUserResponse;
import com.shepherd.shepslibrary.data.model.*;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.*;
import com.shepherd.shepslibrary.service.email.EmailValidationService;
import com.shepherd.shepslibrary.service.email.MailNotificationService;
import com.shepherd.shepslibrary.service.passwordServie.PasswordValidationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final EmailValidationService emailValidationService;
    private final PasswordValidationService passwordValidationService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final MailNotificationService notificationService;
    private static final int MAIL_EXPIRATION_TIME_IN_MIN = 30;

    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        String email = registerUserRequest.getEmail().toLowerCase().trim();
        checkIfUserExists(email);
//        validateEmail(email);
//        validatePassword(registerUserRequest.getPassword());
        User user = new User();
        user.setFirstName(registerUserRequest.getFirstName().trim());
        user.setLastName(registerUserRequest.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setGender(Gender.valueOf(registerUserRequest.getGender().toUpperCase().trim()));
        user.setRole(Role.MEMBER);
        User savedUser = userRepository.save(user);
        String token = tokenService.saveToken(savedUser, TokenType.EMAIL_CONFIRMATION, MAIL_EXPIRATION_TIME_IN_MIN);
        notificationService.sendVerificationMail(savedUser, token);
        log.info("::::: User with the first name {} registered successfully :::::", savedUser.getFirstName());
        return getRegisterUserResponse(savedUser);
    }

    private void checkIfUserExists(String email) {
        if(userRepository.existsByEmail(email))
            throw new AlreadyExistsException("User with the provided email already exists");
    }

    private void validateEmail(String email) {
        if(!emailValidationService.isValidEmail(email))
            throw new EmailValidationException("Your email address is not acceptable");
    }

    private void validatePassword(String password){
        if(passwordValidationService.isPasswordBreached(password))
            throw new PasswordValidationException("This password has been compromised. Use a new, unique password"
                    , BAD_REQUEST.value());
    }

    private static RegisterUserResponse getRegisterUserResponse(User user) {
        return RegisterUserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .isEnabled(user.isEnabled())
                .isRevoked(user.isRevoked())
                .build();
    }

    @Override
    public EmailConfirmationResponse verifyEmail(String token) {
        if(token.isBlank())
            throw new ShepsLibraryException("Token is required");
        ShepsToken shepsToken = tokenService.validateToken(token, TokenType.EMAIL_CONFIRMATION);
        User user = shepsToken.getUser();
        if(!user.isEnabled()){
            user.setEnabled(true);
            User verifiedUser = userRepository.save(user);
            tokenService.deleteToken(shepsToken);
            JwtTokenResponse jwtTokenResponse = tokenService.buildAndSaveJwtToken(verifiedUser);
            return buildEmailConfirmationResponse(verifiedUser, jwtTokenResponse);
        }
        throw new UserAlreadyEnabledException("User is already verified");
    }

    private EmailConfirmationResponse buildEmailConfirmationResponse(User user,JwtTokenResponse jwtTokenResponse) {
        return EmailConfirmationResponse.builder()
                .message("User verified successfully")
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isEnabled(user.isEnabled())
                .accessToken(jwtTokenResponse.getAccessToken())
                .refreshToken(jwtTokenResponse.getRefreshToken())
                .build();
    }
}
