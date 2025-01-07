package com.shepherd.shepslibrary.security;

public final class AllowedURIs {
    public static String[] allowedEndpoints() {
        return new String[]{
                "/api/v1/auth/signup",
                "/api/v1/auth/verify",
                "/api/v1/auth/login",
                "/api/v1/auth/request-password-reset",
                "/api/v1/auth/reset-password"
        };
    }
    private AllowedURIs() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}
