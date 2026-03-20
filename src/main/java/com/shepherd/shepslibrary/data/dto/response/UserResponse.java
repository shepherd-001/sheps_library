package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.Gender;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
public class UserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String role;
    private boolean enabled;
    private boolean emailVerified;
}
