package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(
//    @Email(regexp = RegexPattern.EMAIL, message = ValidationMessage.INVALID_EMAIL)
//    @NotBlank(message = ValidationMessage.BLANK_EMAIL)
//    String email,

    @NotBlank(message = ValidationMessage.BLANK_TOKEN)
    String token
){}