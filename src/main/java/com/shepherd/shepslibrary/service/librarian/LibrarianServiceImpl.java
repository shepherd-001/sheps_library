package com.shepherd.shepslibrary.service.librarian;

import com.shepherd.shepslibrary.data.dto.request.CreatePasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.UserAlreadyEnabledException;
import com.shepherd.shepslibrary.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class LibrarianServiceImpl implements LibrarianService{
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
//    private final PasswordValidationService passwordValidationService;

    @Override
    public AuthResponse createPassword(CreatePasswordRequest request) {
        ShepsToken shepsToken = tokenService.validateToken(request.getToken(), TokenType.LIBRARIAN_INVITATION, request.getEmail());
//        passwordValidationService.validatePasswordNotBreached(request.getPassword());
        User user = shepsToken.getUser();
        if(!user.isEnabled() && user.getPassword() != null)
            throw new UserAlreadyEnabledException("User already created a password");
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        tokenService.deleteToken(shepsToken);
        return tokenService.generateJwtTokens(user);
    }
}
