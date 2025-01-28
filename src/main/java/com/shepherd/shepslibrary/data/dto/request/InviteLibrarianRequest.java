package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InviteLibrarianRequest {
    @NotBlank(message = ValidationMessage.BLANK_FIRST_NAME)
    @Pattern(message = ValidationMessage.INVALID_FIRST_NAME, regexp = RegexPattern.USER_NAME)
    @Max(value = 50, message = ValidationMessage.FIRST_NAME_TOO_LONG)
    private String firstName;

    @NotBlank(message = ValidationMessage.BLANK_LAST_NAME)
    @Pattern(message = ValidationMessage.INVALID_LAST_NAME, regexp = RegexPattern.USER_NAME)
    @Max(value = 50, message = ValidationMessage.LAST_NAME_TOO_LONG)
    private String lastName;

    @NotBlank(message = ValidationMessage.BLANK_EMAIL)
    @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
    private String email;

    private Gender gender;
}
