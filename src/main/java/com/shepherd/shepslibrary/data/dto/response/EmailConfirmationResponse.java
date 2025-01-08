package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailConfirmationResponse {
    private String message;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final boolean isEnabled;
    private String accessToken;
    private String refreshToken;
}
