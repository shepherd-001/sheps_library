package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.exceptions.InvalidJwtException;
import com.shepherd.shepslibrary.exceptions.ShepsTokenException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService{
    private final TokenRepository tokenRepository;
    private final JwtUtils jwtUtils;


    public void logoutCurrentSession(HttpServletRequest request){
        String token = SecurityUtils.extractJwtToken(request);
        String userEmail = SecurityUtils.getAuthenticationName();

        if(!jwtUtils.isValidToken(token, userEmail)){
            log.warn("==>> Invalid JWT provided for user {}", userEmail);
            throw new InvalidJwtException("Invalid or expired token");
        }

        int deleted = tokenRepository.revokeToken(token, TokenType.JWT);
        if(deleted > 0){
            log.info("==>> Deleted current session token for user {}", userEmail);
        }else {
            log.warn("==>> Token not found or already invalidated for user {}", userEmail);
        }
    }

    public void logoutAllSessions(){
        String userEmail = SecurityUtils.getAuthenticationName();
        int deletedToken = tokenRepository.revokeAllTokensForUser(userEmail, TokenType.JWT);
        if(deletedToken > 0){
            log.info("==>> Deleted {} token(s) for user {}", deletedToken, userEmail);
        }else{
            log.warn("==>> No tokens found for user {}", userEmail);
        }
    }
}