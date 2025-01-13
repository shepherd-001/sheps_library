package com.shepherd.shepslibrary.service.token;


import com.shepherd.shepslibrary.data.dto.response.JwtTokenResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;

import java.util.UUID;

public interface TokenService {
    String saveToken(User user, TokenType tokenType, int expirationTimeInMinutes);
    JwtTokenResponse buildAndSaveJwtToken(User user);
    ShepsToken validateToken(String token, TokenType tokenType);
    void deleteToken(ShepsToken shepsToken);
    void deleteAllTokenByUserEmail(String userEmail);
}
