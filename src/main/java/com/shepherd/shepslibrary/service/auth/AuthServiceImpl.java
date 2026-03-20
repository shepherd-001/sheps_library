package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.VerifyEmailRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.dto.response.EmailVerificationResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.UserAlreadyEnabledException;
import com.shepherd.shepslibrary.mapper.UserMapper;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.security.SecurityUtils;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.shepherd.shepslibrary.utils.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
//    private final PasswordValidationService passwordValidationService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final MailNotificationService notificationService;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public EmailVerificationResponse verifyEmail(VerifyEmailRequest request) {
        ShepsToken shepsToken = tokenService.validateToken(request.getToken(), TokenType.EMAIL_CONFIRMATION, request.getEmail());
        User user = shepsToken.getUser();

        if(user.isEmailVerified())
            throw new UserAlreadyEnabledException("User is already verified");
        if(user.isEnabled())
            throw new UserAlreadyEnabledException("User is already enabled");

        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenService.deleteToken(shepsToken);

        return userMapper.mapToEmailVerificationResponse(user, tokenService.generateJwtTokens(user));
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        User user = authenticatedUser.getUser();
        log.info("User {} authenticated successfully", user.getEmail());
        return tokenService.generateJwtTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = SecurityUtils.getCurrentPrincipal().getUser();
        validatePasswordChange(user.getPassword(), changePasswordRequest);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        log.info("==>> Password changed successfully");
        return tokenService.generateJwtTokens(user);
    }

    private void validatePasswordChange(String currentEncodedPassword, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentEncodedPassword))
            throw new BadCredentialsException(INVALID_CURRENT_PASSWORD);

        if (request.getCurrentPassword().equals(request.getNewPassword()))
            throw new BadCredentialsException(SAME_OLD_AND_NEW_PASSWORD);

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new BadCredentialsException(MISMATCH_PASSWORD);

//        passwordValidationService.validatePasswordNotBreached(request.getNewPassword());
    }

    @Override
    public String requestPasswordReset(String email) {
        userRepository.findByEmailEqualsIgnoreCase(email.trim())
                .filter(User::isEnabled)
                .ifPresent(this::sendPasswordResetToken);
        return "If the email exists, a reset password link has been sent to your email address";
    }

    private void sendPasswordResetToken(User user) {
        String token = tokenService.generateToken(user, TokenType.RESET_PASSWORD);
        notificationService.sendResetPasswordMail(user, token);
        log.info("==>> Password reset email sent to: {}", user.getEmail());
    }

    @Override
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        ShepsToken shepsToken = tokenService.validateToken(resetPasswordRequest.getToken(),
                TokenType.RESET_PASSWORD, resetPasswordRequest.getEmail());
//        passwordValidationService.validatePasswordNotBreached(resetPasswordRequest.getNewPassword());
        User user = shepsToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        tokenService.deleteToken(shepsToken);
        userRepository.save(user);
        log.info("==>> Password reset successful for user {}", user.getEmail());
        return tokenService.generateJwtTokens(user);
    }
}
