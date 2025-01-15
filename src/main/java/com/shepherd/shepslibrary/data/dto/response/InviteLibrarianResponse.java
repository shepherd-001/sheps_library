package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InviteLibrarianResponse {
    private String message;
    private String email;
}
