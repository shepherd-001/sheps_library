package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.*;
import com.shepherd.shepslibrary.data.dto.response.*;
import com.shepherd.shepslibrary.data.model.*;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.*;
import com.shepherd.shepslibrary.service.email.EmailValidationService;
import com.shepherd.shepslibrary.service.email.MailNotificationService;
import com.shepherd.shepslibrary.service.passwordServie.PasswordValidationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail().toLowerCase().trim(), loginRequest.getPassword()));
        String userEmail = authentication.getName();
        User user = getUserByEmail(userEmail);
        if(!user.isEnabled())
            throw new ShepsLibraryException("Verify your email address before you proceed");
        JwtTokenResponse jwtTokenResponse = tokenService.buildAndSaveJwtToken(user);
        return LoginResponse.builder()
                .message("User logged in successfully")
                .accessToken(jwtTokenResponse.getAccessToken())
                .refreshToken(jwtTokenResponse.getRefreshToken())
                .build();
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new ResourceNotFoundException("User with the provided email not found"));
    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        log.info("::::: Initiating change password request :::::");
        User user = getCurrentUser();
        checkIfCurrentPasswordIsCorrect(changePasswordRequest.getCurrentPassword(), user.getPassword());
        checkIfCurrentAndNewPasswordAreNotTheSame(changePasswordRequest.getCurrentPassword(), changePasswordRequest.getNewPassword());
        checkIfTwoPasswordAreTheSame(changePasswordRequest.getNewPassword(), changePasswordRequest.getConfirmPassword());
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        User savedUser = userRepository.save(user);
        tokenService.deleteAllTokenByUserEmail(savedUser.getEmail());
        JwtTokenResponse jwtTokenResponse = tokenService.buildAndSaveJwtToken(savedUser);
        return ChangePasswordResponse.builder()
                .message("Password changed successfully")
                .accessToken(jwtTokenResponse.getAccessToken())
                .refreshToken(jwtTokenResponse.getRefreshToken())
                .build();
    }

    private void checkIfCurrentPasswordIsCorrect(String currentPassword, String appUserPassword) {
        if(!passwordEncoder.matches(currentPassword, appUserPassword))
            throw new BadCredentialsException("Current password is invalid");
    }

    private void checkIfCurrentAndNewPasswordAreNotTheSame(String currentPassword, String newPassword){
        if(currentPassword.equals(newPassword))
            throw new BadCredentialsException("New password cannot be the same as old password");
    }

    private void checkIfTwoPasswordAreTheSame(String newPassword, String confirmPassword){
        if(!newPassword.equals(confirmPassword))
            throw new BadCredentialsException("Passwords do not match");
    }

    @Override
    public RequestResetPasswordResponse requestPasswordReset(PasswordResetRequest passwordResetRequest){
        log.info("::::: Initiating request password reset :::::");
        return userRepository.findByEmail(passwordResetRequest.getEmail())
                .filter(User::isEnabled)
                .map(user -> {
                    tokenService.deleteAllTokenByUserEmail(user.getEmail());
                    String token = tokenService.saveToken(user, TokenType.RESET_PASSWORD, MAIL_EXPIRATION_TIME_IN_MIN);
                    notificationService.sendResetPasswordMail(user, token);
                    return requestPasswordResetMessage();
                }).orElse(requestPasswordResetMessage());
    }

    private static RequestResetPasswordResponse requestPasswordResetMessage(){
        return RequestResetPasswordResponse.builder()
                .message("We’ve sent a password reset link to your email address. " +
                "Please follow the instructions in the email to reset your password.")
                .build();
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("::::: Initiating password reset :::::");
        ShepsToken shepsToken = tokenService.validateToken(resetPasswordRequest.getToken(), TokenType.RESET_PASSWORD);
        User user = shepsToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        tokenService.deleteToken(shepsToken);
        User savedUser = userRepository.save(user);
        JwtTokenResponse jwtTokenResponse = tokenService.buildAndSaveJwtToken(savedUser);
        return ResetPasswordResponse.builder()
                .message("Password reset successful")
                .accessToken(jwtTokenResponse.getAccessToken())
                .refreshToken(jwtTokenResponse.getRefreshToken())
                .build();
    }
}
