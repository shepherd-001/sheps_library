package com.shepherd.shepslibrary.service.token;

import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.exceptions.ShepsTokenException;
import com.shepherd.shepslibrary.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService{
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;
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
        String token = jwtUtils.generateAccessToken(user, expirationTimeInSeconds);
        ShepsToken shepsToken = ShepsToken.builder()
                .user(user)
                .tokenType(tokenType)
                .token(token)
                .isExpired(false)
                .isRevoked(false)
                .expirationTime(Instant.now().plusSeconds(expirationTimeInSeconds))
                .build();

         revokeAllUserTokens(user.getId(), tokenType);
        tokenRepository.save(shepsToken);
        log.info("==>> Created a new {} token for user {}", tokenType, user.getEmail());
        return token;
    }

    @Override
    public AuthResponse generateJwtTokens(User user) {
        String accessToken = jwtUtils.generateAccessToken(user, accessTokenExpiration);
        String refreshToken = jwtUtils.generateRefreshToken(user, refreshTokenExpiration);
        ShepsToken shepsToken = ShepsToken.builder()
                .user(user)
                .tokenType(TokenType.JWT)
                .token(accessToken)
                .refreshToken(refreshToken)
                .isExpired(false)
                .isRevoked(false)
                .build();

        tokenRepository.save(shepsToken);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public ShepsToken validateToken(String token, TokenType tokenType) {
        if (!jwtUtils.isValidToken(token)){
            log.error("==>> Not a valid JWT token.");
            throw new ShepsTokenException("Token is invalid");
        }
        ShepsToken shepsToken = fetchToken(token, tokenType);
        validateTokenExpiration(shepsToken);
        String expectedEmail = jwtUtils.extractUsername(token);
        validateUserEmail(shepsToken.getUser().getEmail(), expectedEmail);
        log.info("==>> Token validation successful");
        return shepsToken;
    }

    private ShepsToken fetchToken(String token, TokenType tokenType) {
        return tokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new ShepsTokenException("Token is invalid"));
    }

    private void validateTokenExpiration(ShepsToken shepsToken) {
        if(shepsToken.getExpirationTime() != null &&
                shepsToken.getExpirationTime().isBefore(Instant.now())){
            throw new ShepsTokenException("Token is expired");
        }
    }

    private void validateUserEmail(String actualEmail, String expectedEmail) {
        if (!actualEmail.equalsIgnoreCase(expectedEmail)) {
            log.error("==>> Token validation failed: email mismatch");
            throw new ShepsTokenException("Token is invalid");
        }
    }

    @Override
    public void deleteToken(ShepsToken shepsToken) {
        tokenRepository.delete(shepsToken);
        log.info("Deleted a token");
    }


    @Override
    public void revokeAllUserTokens(String userId, TokenType tokenType) {
        int revoked = tokenRepository.revokeAllTokensForUser(userId, tokenType);
        log.info("==>> Revoked {} tokens", revoked);
    }

    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void deleteExpiredAndRevokedTokens() {
        Instant cutoff = Instant.now().minus(1, ChronoUnit.DAYS);
        int deleted = tokenRepository.deleteAllRevokedOrExpiredTokensOlderThan(cutoff);
        log.info("Deleted {} revoked/expired tokens older than 1 day(s)", deleted);
    }
}