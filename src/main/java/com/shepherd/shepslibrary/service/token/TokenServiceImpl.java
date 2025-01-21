package com.shepherd.shepslibrary.service.token;

import com.shepherd.shepslibrary.data.dto.response.JwtTokenResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.exceptions.ShepsTokenException;
import com.shepherd.shepslibrary.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService{
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    @Value("${jwt_access_expiration}")
    private long accessTokenExpiration;
    @Value("${jwt_refresh_expiration}")
    private long refreshTokenExpiration;
    @Value("${reset_password_expiration}")
    private long resetPasswordExpiration;
    @Value("${email_confirmation_expiration}")
    private long emailConfirmationExpiration;
    @Value("${librarian_invite_expiration}")
    private long librarianInviteExpiration;
    private static final long DEFAULT_EXPIRATION_TIME = 1800L;


    private long getExpirationTime(TokenType tokenType){
        return switch (tokenType){
            case RESET_PASSWORD -> resetPasswordExpiration;
            case EMAIL_CONFIRMATION -> emailConfirmationExpiration;
            case LIBRARIAN_INVITATION -> librarianInviteExpiration;
            default -> DEFAULT_EXPIRATION_TIME;
        };
    }

    @Override
    @Transactional
    public String generateToken(User user, TokenType tokenType) {
        long expirationTimeInSeconds = getExpirationTime(tokenType);
        log.info("::::: Initiating the creation of a new {} token :::::", tokenType);
        String token = jwtService.generateAccessToken(user.getEmail(), expirationTimeInSeconds);
        ShepsToken shepsToken = ShepsToken.builder()
                .user(user)
                .tokenType(tokenType)
                .token(token)
                .isExpired(false)
                .isRevoked(false)
                .expirationTime(LocalDateTime.now().plusSeconds(expirationTimeInSeconds))
                .build();

        deleteAllTokenByUserAndType(user.getEmail(), tokenType);
        tokenRepository.save(shepsToken);
        log.info("::::: Created a new {} token :::::", tokenType);
        return token;
    }

    @Override
    public JwtTokenResponse generateJwtTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail(), accessTokenExpiration);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), refreshTokenExpiration);
        ShepsToken shepsToken = ShepsToken.builder()
                .user(user)
                .tokenType(TokenType.JWT)
                .token(accessToken)
                .refreshToken(refreshToken)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(shepsToken);
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public ShepsToken validateToken(String token, TokenType tokenType) {
        ShepsToken shepsToken = tokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(()-> new ShepsTokenException("Token is invalid"));
        validateUserEmail(token, shepsToken.getUser().getEmail());
        if (shepsToken.getExpirationTime() == null ||
                shepsToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            log.info("::::: Token is expired or expiration time is null :::::");
            throw new ShepsTokenException("Token is expired");
        }
        log.info("::::: Token validation successful :::::");
        return shepsToken;
    }

    private void validateUserEmail(String token, String email) {
        String jwtEmail = jwtService.extractUsername(token);
        if(!jwtEmail.equals(email)){
            log.error("::::: JWT email '{}' does not match expected email '{}' :::::", jwtEmail, email);
            throw new ShepsTokenException("Error validating token");
        }
    }

    @Override
    public void deleteToken(ShepsToken shepsToken) {
        tokenRepository.delete(shepsToken);
        log.info("::::: Deleted a token :::::");
    }

    @Override
    @Transactional
    public void deleteAllTokenByUserAndType(String userEmail, TokenType tokenType) {
        log.info("::::: Initiating the removal of a token by user email :::::");
        tokenRepository.deleteAllByUserEmailAndTokenType(userEmail, tokenType);
        log.info("::::: Deleted all tokens by user email :::::");
    }
}

