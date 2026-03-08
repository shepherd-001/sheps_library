package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerifyEmailRequest {
    @NotBlank(message = ValidationMessage.BLANK_EMAIL)
    private String email;
    @NotBlank(message = ValidationMessage.BLANK_TOKEN)
    @Email(regexp = RegexPattern.EMAIL, message = ValidationMessage.INVALID_EMAIL)
    private String token;
}
