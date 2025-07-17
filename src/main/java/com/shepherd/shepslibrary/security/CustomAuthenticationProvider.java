package com.shepherd.shepslibrary.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.shepherd.shepslibrary.utils.ErrorMessage.INVALID_EMAIL_OR_PASSWORD;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = Objects.toString(authentication.getPrincipal(), "").trim();
        String rawPassword = Objects.toString(authentication.getCredentials(), "");

        if (email.isEmpty() || rawPassword.isEmpty()) {
            log.warn("Authentication failed: empty email or password");
            throw new BadCredentialsException(INVALID_EMAIL_OR_PASSWORD);
        }
        UserDetails userDetails;
        try{
            userDetails = userDetailsService.loadUserByUsername(email);
        }catch (UsernameNotFoundException ex){
            throw new BadCredentialsException(INVALID_EMAIL_OR_PASSWORD);
        }
        if (passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
        }
        throw new BadCredentialsException(INVALID_EMAIL_OR_PASSWORD);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
