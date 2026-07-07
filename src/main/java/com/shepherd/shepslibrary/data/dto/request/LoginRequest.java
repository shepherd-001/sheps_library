package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = ValidationMessage.BLANK_EMAIL)
        @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
        String email,

        @NotBlank(message = ValidationMessage.BLANK_PASSWORD)
        @Pattern(regexp = RegexPattern.PASSWORD, message = ValidationMessage.INVALID_PASSWORD)
        String password
){}