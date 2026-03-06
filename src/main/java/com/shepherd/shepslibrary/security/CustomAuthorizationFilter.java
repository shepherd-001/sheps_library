package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {

        final String jwtToken = extractJwtToken(request);
        if(!StringUtils.hasText(jwtToken) ||
                SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String userEmail;
        try{
            userEmail = jwtUtils.extractUsername(jwtToken);
            if(!StringUtils.hasText(userEmail)){
                log.warn("Token validation failed: no subject found");
                filterChain.doFilter(request, response);
                return;
            }

            if(!isTokenValid(jwtToken) || !jwtUtils.isValidToken(jwtToken, userEmail)){
                log.warn("JWT token is invalid, revoked, or expired for user: {}", userEmail);
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if(!isUserAccountValid(userDetails)){
                log.warn("Account invalid for user: {}", userEmail);
                filterChain.doFilter(request, response);
                return;
            }

            setSecurityContext(request, userDetails);
            log.info("User authenticated: {}", userEmail);

        }catch (Exception ex){
            log.error("Authorization error: {}", ex.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, String.format("Unauthorized: %s", ex.getMessage()));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        return (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX))
                ? authHeader.substring(BEARER_PREFIX_LENGTH)
                : null;
    }

    private boolean isTokenValid(String jwtToken) {
        return tokenRepository.findByTokenAndTokenType(jwtToken, TokenType.JWT)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);
    }

    private boolean isUserAccountValid(UserDetails userDetails) {
        return userDetails.isEnabled() &&
                userDetails.isAccountNonLocked() &&
                userDetails.isCredentialsNonExpired() &&
                userDetails.isAccountNonExpired();
    }

    private void setSecurityContext(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}