package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.Gender;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterUserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private boolean isEnabled;
    private boolean isRevoked;
}
