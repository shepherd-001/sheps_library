package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.UserNotVerifiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailEqualsIgnoreCaseWithRole(email.trim())
                .orElseThrow(() -> {
                    log.info("Login attempt failed: User with email '{}' not found", email);
                    return new UsernameNotFoundException("User not found");
                });
        return AuthenticatedUser.builder()
                .user(user)
                .build();
    }
}