package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
