package com.shepherd.shepslibrary.service.token;


import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;

public interface TokenService {
    String saveToken(User user, TokenType tokenType, int expirationTimeInMinutes);
    void saveToken(ShepsToken shepsToken);
    ShepsToken validateToken(String token, String email, TokenType tokenType);
    void deleteToken(ShepsToken shepsToken);
}
