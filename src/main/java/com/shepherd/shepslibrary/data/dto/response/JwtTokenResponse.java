package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.TokenType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
}
