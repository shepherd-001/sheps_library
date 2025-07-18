package com.shepherd.shepslibrary.service.auth;

import com.shepherd.shepslibrary.data.dto.request.ChangePasswordRequest;
import com.shepherd.shepslibrary.data.dto.request.LoginRequest;
import com.shepherd.shepslibrary.data.dto.request.ResetPasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.dto.response.EmailConfirmationResponse;
import com.shepherd.shepslibrary.data.dto.response.JwtTokenResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.exceptions.UserAlreadyEnabledException;
import com.shepherd.shepslibrary.mapper.UserMapper;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.service.passwordServie.PasswordValidationService;
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

import static com.shepherd.shepslibrary.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordValidationService passwordValidationService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final MailNotificationService notificationService;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public EmailConfirmationResponse verifyEmail(String token, String email) {
        ShepsToken shepsToken = tokenService.validateToken(token, TokenType.EMAIL_CONFIRMATION, email);
        User user = shepsToken.getUser();

        if(user.isEnabled())
            throw new UserAlreadyEnabledException("User is already verified");

        user.setEnabled(true);
        userRepository.save(user);
        tokenService.deleteToken(shepsToken);

        return userMapper.mapToEmailConfirmationResponse(user, tokenService.generateJwtTokens(user));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticateUser(loginRequest);
        User user = getUserByEmail(authentication.getName());

        if(!user.isEnabled())
            throw new ShepsLibraryException("Verify your email address before you proceed");

        tokenService.deleteAllTokenByUserAndType(user.getEmail(), TokenType.JWT);
        return generateJwtTokens(user);
    }

    private Authentication authenticateUser(LoginRequest loginRequest) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail().trim(), loginRequest.getPassword()));
    }

    private AuthResponse generateJwtTokens(User user) {
        JwtTokenResponse jwtTokenResponse = tokenService.generateJwtTokens(user);
        return AuthResponse.builder()
                .accessToken(jwtTokenResponse.getAccessToken())
                .refreshToken(jwtTokenResponse.getRefreshToken())
                .build();
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmailEqualsIgnoreCase(userEmail)
                .orElseThrow(()-> new ResourceNotFoundException("User with the provided email not found"));
    }

    @Override
    @Transactional
    public AuthResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        log.info("Change password request initiated for user");
        User user = getCurrentUser();
        validatePasswordChange(user.getPassword(), changePasswordRequest);

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        tokenService.deleteAllTokenByUserAndType(user.getEmail(), TokenType.JWT);
        return generateJwtTokens(user);
    }

    private void validatePasswordChange(String currentEncodedPassword, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentEncodedPassword))
            throw new BadCredentialsException("Invalid current password");

        if (request.getCurrentPassword().equals(request.getNewPassword()))
            throw new BadCredentialsException("New password cannot be the same as the current password");

        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new BadCredentialsException("Passwords do not match");

        passwordValidationService.validatePasswordNotBreached(request.getNewPassword());
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
        log.info("Password reset token sent to: {}", user.getEmail());
    }

    @Override
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("::::: Initiating password reset :::::");
        ShepsToken shepsToken = tokenService.validateToken(resetPasswordRequest.getToken(),
                TokenType.RESET_PASSWORD, resetPasswordRequest.getEmail());
        tokenService.deleteToken(shepsToken);
        passwordValidationService.validatePasswordNotBreached(resetPasswordRequest.getNewPassword());
        User user = shepsToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return generateJwtTokens(user);
    }
}
