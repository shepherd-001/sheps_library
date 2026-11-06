package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.data.model.*;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {
    @Test void refreshTokenCannotBeUsedAsAccessToken() {
        TokenRepository repository = mock(TokenRepository.class);
        JwtUtils jwt = new JwtUtils(Jwts.SIG.HS256.key().build(), repository);
        jwt.init();
        User user = User.builder().email("reader@example.com")
                .role(UserRole.builder().name("MEMBER").build()).build();
        when(repository.findTokenVersionByEmail(user.getEmail())).thenReturn(0);
        assertTrue(jwt.isValidToken(jwt.generateAccessToken(user, 60), user.getEmail()));
        assertFalse(jwt.isValidToken(jwt.generateRefreshToken(user, 60), user.getEmail()));
    }

    @Test void passwordChangeInvalidatesEarlierTokens() {
        TokenRepository repository = mock(TokenRepository.class);
        JwtUtils jwt = new JwtUtils(Jwts.SIG.HS256.key().build(), repository);
        jwt.init();
        User user = User.builder().email("reader@example.com")
                .role(UserRole.builder().name("MEMBER").build()).build();
        String token = jwt.generateAccessToken(user, 60);
        when(repository.findTokenVersionByEmail(user.getEmail())).thenReturn(1);
        assertFalse(jwt.isValidToken(token, user.getEmail()));
    }
}
