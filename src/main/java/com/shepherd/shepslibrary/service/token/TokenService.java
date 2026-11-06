package com.shepherd.shepslibrary.service.token;


import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.enums.TokenType;
import com.shepherd.shepslibrary.data.model.User;

public interface TokenService {
    String generateToken(User user, TokenType tokenType);
    AuthResponse generateJwtTokens(User user);
    /** Must run inside the transaction that consumes/deletes the one-time token. */
    ShepsToken validateToken(String token, TokenType tokenType);
    void deleteToken(ShepsToken shepsToken);
    void revokeAllUserTokens(String userEmail, TokenType tokenType);
}