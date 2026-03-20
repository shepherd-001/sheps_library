package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class RegisterUserResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private boolean enabled;
    private boolean emailVerified;
}
