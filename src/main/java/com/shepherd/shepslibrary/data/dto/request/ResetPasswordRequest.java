package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
//    @NotBlank(message = ValidationMessage.BLANK_EMAIL)
//    @Email(regexp = RegexPattern.EMAIL, message = ValidationMessage.INVALID_EMAIL)
//    private String email;

    @NotBlank(message = ValidationMessage.BLANK_TOKEN)
    String token,

    @NotBlank(message = ValidationMessage.BLANK_PASSWORD)
    @Pattern(message = ValidationMessage.INVALID_PASSWORD, regexp = RegexPattern.PASSWORD)
    String newPassword
){}
