package com.shepherd.shepslibrary.service.token;


import com.shepherd.shepslibrary.data.dto.response.JwtTokenResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;

public interface TokenService {
    String generateToken(User user, TokenType tokenType);
    JwtTokenResponse generateJwtTokens(User user);
    ShepsToken validateToken(String token, TokenType tokenType, String expectedEmail);
    void deleteToken(ShepsToken shepsToken);
    void deleteAllTokenByUserAndType(String userEmail, TokenType tokenType);
}
