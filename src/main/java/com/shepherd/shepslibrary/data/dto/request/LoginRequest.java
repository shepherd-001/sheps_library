package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//public class LoginRequest {
//    @NotBlank(message = ValidationMessage.BLANK_EMAIL)
//    @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
//    private String email;
//
//    @NotBlank(message = ValidationMessage.BLANK_PASSWORD)
//    @Pattern(regexp = RegexPattern.PASSWORD, message = ValidationMessage.INVALID_PASSWORD)
//    private String password;
//}

public record LoginRequest(
        @NotBlank(message = ValidationMessage.BLANK_EMAIL)
        @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
        String email,

        @NotBlank(message = ValidationMessage.BLANK_PASSWORD)
        @Pattern(regexp = RegexPattern.PASSWORD, message = ValidationMessage.INVALID_PASSWORD)
        String password
){}