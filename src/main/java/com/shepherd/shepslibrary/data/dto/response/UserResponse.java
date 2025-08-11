package com.shepherd.shepslibrary.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private Role role;
    private String role;
    private boolean isEnabled;
    private boolean isRevoked;
}
