package com.shepherd.shepslibrary.service.librarian;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.CreatePasswordRequest;
import com.shepherd.shepslibrary.data.dto.response.AuthResponse;
import com.shepherd.shepslibrary.data.dto.response.JwtTokenResponse;
import com.shepherd.shepslibrary.data.model.ShepsToken;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.PasswordValidationException;
import com.shepherd.shepslibrary.exceptions.UserAlreadyEnabledException;
import com.shepherd.shepslibrary.service.passwordServie.PasswordValidationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibrarianServiceImpl implements LibrarianService{
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidationService passwordValidationService;

    @Override
    public BaseResponse<AuthResponse> createPassword(CreatePasswordRequest request) {
        ShepsToken shepsToken = tokenService.validateToken(request.getToken(), TokenType.LIBRARIAN_INVITATION, request.getEmail());
//        validatePassword(request.getPassword());
        User user = shepsToken.getUser();
        if(!user.isEnabled() && user.getPassword() == null){
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            User verifiedUser = userRepository.save(user);
            updateUserCache(verifiedUser);
            tokenService.deleteToken(shepsToken);
            JwtTokenResponse jwtTokenResponse = tokenService.generateJwtTokens(verifiedUser);
            return getCreatePasswordResponse(jwtTokenResponse);
        }
        throw new UserAlreadyEnabledException("User already created password");
    }

    private void validatePassword(String password){
        if(passwordValidationService.isPasswordBreached(password))
            throw new PasswordValidationException("This password has been compromised. Use a new, unique password"
                    , BAD_REQUEST.value());
    }

    @CachePut(value = "userCache", key = "#user.email")
    public void updateUserCache(User user) {
        log.info("::::: Updating cache for user with email: {} :::::", user.getEmail());
    }

    private static BaseResponse<AuthResponse> getCreatePasswordResponse(JwtTokenResponse jwtTokenResponse){
        return BaseResponse.buildResponse("Librarian password created successfully", AuthResponse.builder()
                        .accessToken(jwtTokenResponse.getAccessToken())
                        .refreshToken(jwtTokenResponse.getRefreshToken())
                .build());
    }
}
