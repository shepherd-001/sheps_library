package com.shepherd.shepslibrary.data.dto.response;

public record EmailVerificationResponse(
        String firstName,
        String lastName,
        String email,
        boolean enabled,
        boolean emailVerified,
        String accessToken,
        String refreshToken
) {}
