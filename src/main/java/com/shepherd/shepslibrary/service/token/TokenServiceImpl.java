package com.shepherd.shepslibrary.service.token;

import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.exceptions.ShepsTokenException;
import com.shepherd.shepslibrary.utils.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService{
    private final TokenRepository tokenRepository;
    private static final int TOKEN_LENGTH = 100;


    @Override
    public String saveToken(User user, TokenType tokenType, int expirationTimeInMinutes) {
        log.info("::::: Initiating the creation of new token :::::");

        String token = RandomGenerator.generateRandomString(TOKEN_LENGTH);
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
    public void saveToken(ShepsToken shepsToken) {
        tokenRepository.save(shepsToken);
    }

    @Override
    public ShepsToken validateToken(String token, String email, TokenType tokenType) {
        ShepsToken shepsToken = tokenRepository.findByUserAndTokenAndTokenType(email, token, tokenType);
        if(shepsToken == null){
            log.info("::::: Token not found :::::");
            throw new ShepsTokenException("Token is invalid or expired");
        }else if(shepsToken.getExpirationTime().isBefore(LocalDateTime.now())){
            log.info("::::: Token is expired :::::");
            throw new ShepsTokenException("Token is expired");
        }
        return shepsToken;
    }

    @Override
    public void deleteToken(ShepsToken shepsToken) {
        tokenRepository.delete(shepsToken);
        log.info("::::: Deleted a token :::::");
    }
}

