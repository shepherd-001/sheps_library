package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.exceptions.ShepsTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService{
    private final TokenRepository tokenRepository;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
    private final JwtUtils jwtUtils;


    public void logoutCurrentSession(String authHeader, String userEmail){
        if(authHeader == null || !authHeader.startsWith(BEARER_PREFIX) || authHeader.length() <= BEARER_PREFIX_LENGTH){
            log.warn("==>> Authorization header is missing or invalid for user {}", userEmail);
            throw new ShepsTokenException("Invalid Authorization header");
        }

        String token = authHeader.substring(BEARER_PREFIX_LENGTH);

        if(!jwtUtils.isValidToken(token, userEmail)){
            log.warn("==>> Invalid JWT provided for user {}", userEmail);
            throw new ShepsTokenException("Invalid or expired token");
        }

        int deleted = tokenRepository.revokeToken(token, TokenType.JWT);
        if(deleted > 0){
            log.info("==>> Deleted current session token for user {}", userEmail);
        }else {
            log.warn("==>> Token not found or already invalidated for user {}", userEmail);
        }
    }

    public void logoutAllSessions(User user){
        int deletedToken = tokenRepository.revokeAllTokensForUser(user.getId(), TokenType.JWT);
        if(deletedToken > 0){
            log.info("==>> Deleted {} token(s) for user {}", deletedToken, user.getEmail());
        }else{
            log.warn("==>> No tokens found for user {}", user.getEmail());
        }
    }
}