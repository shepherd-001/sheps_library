package com.shepherd.shepslibrary.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
}
