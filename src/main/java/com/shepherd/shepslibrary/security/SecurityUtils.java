package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.exceptions.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.shepherd.shepslibrary.utils.ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS;


public final class SecurityUtils {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    public static String extractJwtToken(HttpServletRequest request){
        String header =  request.getHeader(HttpHeaders.AUTHORIZATION);

        if(header == null || !header.startsWith(BEARER_PREFIX) || header.length() <= BEARER_PREFIX_LENGTH){
            throw new UnauthorizedException("Invalid or missing Authorization header");
        }
        return header.substring(BEARER_PREFIX_LENGTH);
    }

    public static AuthenticatedUser getCurrentPrincipal(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null ||
        !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken){
            throw new UnauthorizedException("User is not authenticated");
        }
        if(!(authentication.getPrincipal() instanceof AuthenticatedUser principal)){
            throw new UnauthorizedException("Invalid user principal");
        }
        return principal;
    }

    public static String getAuthenticationName(){
        return getCurrentPrincipal().getUsername();
    }

    private SecurityUtils() {
        throw new UnsupportedOperationException(NON_INSTANTIABLE_UTILITY_CLASS);
    }
}