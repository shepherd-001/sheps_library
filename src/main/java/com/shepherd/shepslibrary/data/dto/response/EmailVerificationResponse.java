package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailVerificationResponse {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final boolean enabled;
    private final boolean emailVerified;
    private String accessToken;
    private String refreshToken;
}
