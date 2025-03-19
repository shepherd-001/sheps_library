package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.*;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.*;
import com.shepherd.shepslibrary.service.notification.EmailValidationService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.service.passwordServie.PasswordValidationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.shepherd.shepslibrary.utils.AppUtils.getCurrentUser;
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
    private final AuthenticationManager authenticationManager;
    private final MailNotificationService notificationService;

    @Override
    @Transactional
    public BaseResponse<RegisterUserResponse> registerUser(RegisterUserRequest registerUserRequest) {
        String email = registerUserRequest.getEmail().toLowerCase().trim();
        validateEmailAddress(email);
//        validatePassword(registerUserRequest.getPassword());
        User user = new User();
        user.setFirstName(registerUserRequest.getFirstName().trim());
        user.setLastName(registerUserRequest.getLastName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setGender(registerUserRequest.getGender());
        user.setRole(Role.MEMBER);
        User savedUser = userRepository.save(user);
        String token = tokenService.generateToken(savedUser, TokenType.EMAIL_CONFIRMATION);
        notificationService.sendVerificationMail(savedUser, token);
        log.info("::::: User with the first name {} registered successfully :::::", savedUser.getFirstName());
        return BaseResponse.buildResponse("User registered successfully", getRegisterUserResponse(savedUser));
    }

    private void validateEmailAddress(String email) {
        if(userRepository.existsByEmailEqualsIgnoreCase(email.trim()))
            throw new AlreadyExistsException("User with the provided email already exists");

//        if(!emailValidationService.isValidEmail(email))
//            throw new EmailValidationException("Your email address is not acceptable");
    }

    private void validatePassword(String password){
        if(passwordValidationService.isPasswordBreached(password))
            throw new PasswordValidationException("This password has been compromised. Use a new, unique password"
                    , BAD_REQUEST.value());
    }

    private static RegisterUserResponse getRegisterUserResponse(User user) {
        return RegisterUserResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .isEnabled(user.isEnabled())
                .isRevoked(user.isRevoked())
                .build();
    }

    @Override
    @Transactional
    public BaseResponse<EmailConfirmationResponse> verifyEmail(String token, String email) {
        ShepsToken shepsToken = tokenService.validateToken(token, TokenType.EMAIL_CONFIRMATION, email);
        User user = shepsToken.getUser();
        if(!user.isEnabled()){
            user.setEnabled(true);
            User verifiedUser = userRepository.save(user);
            updateUserCache(verifiedUser);
            tokenService.deleteToken(shepsToken);
            JwtTokenResponse jwtTokenResponse = tokenService.generateJwtTokens(verifiedUser);
            return buildEmailConfirmationResponse(verifiedUser, jwtTokenResponse);
        }
        throw new UserAlreadyEnabledException("User is already verified");
    }

    private BaseResponse<EmailConfirmationResponse> buildEmailConfirmationResponse(User user,JwtTokenResponse jwtTokenResponse) {
        EmailConfirmationResponse emailConfirmationResponse = EmailConfirmationResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isEnabled(user.isEnabled())
                .accessToken(jwtTokenResponse.getAccessToken())
                .refreshToken(jwtTokenResponse.getRefreshToken())
                .build();
        return BaseResponse.buildResponse("User verified successfully", emailConfirmationResponse);
    }

    @Override
    @Transactional
    public BaseResponse<AuthResponse> login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail().trim(), loginRequest.getPassword()));
        String userEmail = authentication.getName();
        User user = getUserByEmail(userEmail);
        if(!user.isEnabled())
            throw new ShepsLibraryException("Verify your email address before you proceed");
        tokenService.deleteAllTokenByUserAndType(userEmail, TokenType.JWT);
        JwtTokenResponse jwtTokenResponse = tokenService.generateJwtTokens(user);
        return BaseResponse.buildResponse("User logged in successfully",
                AuthResponse.builder()
                        .accessToken(jwtTokenResponse.getAccessToken())
                        .refreshToken(jwtTokenResponse.getRefreshToken())
                        .build());
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmailEqualsIgnoreCase(userEmail)
                .orElseThrow(()-> new ResourceNotFoundException("User with the provided email not found"));
    }

    @Override
    @Transactional
    public BaseResponse<ChangePasswordResponse> changePassword(ChangePasswordRequest changePasswordRequest) {
        log.info("::::: Initiating change password request :::::");
        User user = getCurrentUser();
        validatePasswordChange(user.getPassword(), changePasswordRequest);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        User savedUser = userRepository.save(user);
        updateUserCache(savedUser);
        tokenService.deleteAllTokenByUserAndType(savedUser.getEmail(), TokenType.JWT);
        JwtTokenResponse jwtTokenResponse = tokenService.generateJwtTokens(savedUser);
        return BaseResponse.buildResponse("Password changed successfully",
                ChangePasswordResponse.builder()
                        .accessToken(jwtTokenResponse.getAccessToken())
                        .refreshToken(jwtTokenResponse.getRefreshToken())
                        .build());
    }

    private void validatePasswordChange(String currentEncodedPassword, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentEncodedPassword))
            throw new BadCredentialsException("Invalid current password");

        if (request.getCurrentPassword().equals(request.getNewPassword()))
            throw new BadCredentialsException("New password cannot be the same as the old password");

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new BadCredentialsException("Passwords do not match");

        validatePassword(request.getNewPassword());
    }

    @CachePut(value = "userCache", key = "#user.email")
    public void updateUserCache(User user) {
        log.info("::::: Updating cache for user with email: {} :::::", user.getEmail());
    }

    @Override
    public BaseResponse<String> requestPasswordReset(String email){
        log.info("::::: Initiating request password reset :::::");
        return userRepository.findByEmailEqualsIgnoreCase(email.trim())
                .filter(User::isEnabled)
                .map(user -> {
                    String token = tokenService.generateToken(user, TokenType.RESET_PASSWORD);
                    notificationService.sendResetPasswordMail(user, token);
                    return requestPasswordResetMessage();
                }).orElse(requestPasswordResetMessage());
    }

    private static BaseResponse<String> requestPasswordResetMessage(){
        return BaseResponse.buildResponse("If the email exists, a reset " +
                "password link has been sent to your email address");
    }

    @Override
    @Transactional
    public BaseResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("::::: Initiating password reset :::::");
        ShepsToken shepsToken = tokenService.validateToken(resetPasswordRequest.getToken(),
                TokenType.RESET_PASSWORD, resetPasswordRequest.getEmail());
        validatePassword(resetPasswordRequest.getNewPassword());
        User user = shepsToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        tokenService.deleteToken(shepsToken);
        User savedUser = userRepository.save(user);
        updateUserCache(savedUser);
        JwtTokenResponse jwtTokenResponse = tokenService.generateJwtTokens(savedUser);
        return BaseResponse.buildResponse("Password reset successful",
                ResetPasswordResponse.builder()
                        .accessToken(jwtTokenResponse.getAccessToken())
                        .refreshToken(jwtTokenResponse.getRefreshToken())
                        .build());
    }
}
