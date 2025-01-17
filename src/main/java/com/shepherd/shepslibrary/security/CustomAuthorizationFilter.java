package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.repository.TokenRepository;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        log.info("::::: Request URI: {} :::::", requestURI);
        for (String endpoint : AllowedURIs.allowedEndpoints()) {
            if (matchesUri(requestURI, endpoint)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try{
            String jwtToken = extractJwtToken(request);
            if(jwtToken != null && isTokenValid(jwtToken)){
                String userEmail = jwtService.extractUsername(jwtToken);
                authenticateUser(request, userEmail, jwtToken);
            }
        }catch (Exception ex){
            log.error("::::: Token validation failed: {} :::::", ex.getMessage());
            throw new ShepsLibraryException(ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX))
            return authHeader.substring(BEARER_PREFIX.length());
        return null;
    }

    private boolean isTokenValid(String jwtToken) {
        return tokenRepository.findByTokenAndTokenType(jwtToken, TokenType.JWT)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);
    }

    private void authenticateUser(HttpServletRequest request, String userEmail, String jwtToken) {
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isValidToken(jwtToken, userEmail) && userDetails.isEnabled())
                setAuthentication(request, userDetails);
        }
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private boolean matchesUri(String requestURI, String allowedUri) {
        if (allowedUri.endsWith("/**")) {
            String baseUri = allowedUri.replace("/**", "");
            return requestURI.startsWith(baseUri);
        }
        return requestURI.equals(allowedUri);
    }

}
