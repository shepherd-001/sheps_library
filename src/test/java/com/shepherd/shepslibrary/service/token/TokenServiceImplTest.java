package com.shepherd.shepslibrary.service.token;

import com.shepherd.shepslibrary.common.exceptions.ShepsTokenException;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.enums.TokenType;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.security.JwtUtils;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceImplTest {
    @Test void revokedPasswordResetTokenIsRejectedBeforeUserAccess() {
        TokenRepository repository = mock(TokenRepository.class);
        JwtUtils jwt = mock(JwtUtils.class);
        TokenServiceImpl service = new TokenServiceImpl(repository, jwt);
        when(jwt.isValidToken("old-token")).thenReturn(true);
        when(repository.findForConsumption("old-token", TokenType.RESET_PASSWORD))
                .thenReturn(Optional.of(ShepsToken.builder().isRevoked(true).build()));
        assertThrows(ShepsTokenException.class,
                () -> service.validateToken("old-token", TokenType.RESET_PASSWORD));
        verify(jwt, never()).extractUsername(anyString());
    }
}
