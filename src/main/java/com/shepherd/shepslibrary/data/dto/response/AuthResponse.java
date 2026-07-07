package com.shepherd.shepslibrary.data.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
