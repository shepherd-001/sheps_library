package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResetPasswordResponse {
    private String accessToken;
    private String refreshToken;
}
