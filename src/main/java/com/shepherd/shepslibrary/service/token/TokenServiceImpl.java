package com.shepherd.shepslibrary.service.token;

import com.shepherd.shepslibrary.data.dto.response.JwtTokenResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.exceptions.ShepsTokenException;
import com.shepherd.shepslibrary.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService{
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;


    @Override
    public String saveToken(User user, TokenType tokenType, int expirationTimeInMinutes) {
        log.info("::::: Initiating the creation of new token :::::");
        String token = jwtService.generateAccessToken(user.getEmail());
        ShepsToken shepsToken = ShepsToken.builder()
                .user(user)
                .tokenType(tokenType)
                .token(token)
                .isExpired(false)
                .isRevoked(false)
                .expirationTime(LocalDateTime.now().plusMinutes(expirationTimeInMinutes))
                .build();
        var tokens = tokenRepository.findAllByUserIdAndTokenType(user.getId(), tokenType);
        tokenRepository.deleteAll(tokens);

        tokenRepository.save(shepsToken);
        log.info("::::: Created a new token :::::");
        return token;
    }

    @Override
    public JwtTokenResponse buildAndSaveJwtToken(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
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

        if (shepsToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            log.info("::::: Token is expired :::::");
            throw new ShepsTokenException("Token is expired");
        }
        log.info("::::: Token validation successful :::::");
        return shepsToken;
    }

    @Override
    public void deleteToken(ShepsToken shepsToken) {
        tokenRepository.delete(shepsToken);
        log.info("::::: Deleted a token :::::");
    }
}

