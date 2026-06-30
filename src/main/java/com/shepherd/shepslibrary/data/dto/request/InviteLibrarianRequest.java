package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import com.shepherd.shepslibrary.utils.validator.EnumValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InviteLibrarianRequest(
    @NotBlank(message = ValidationMessage.BLANK_FIRST_NAME)
    @Pattern(message = ValidationMessage.INVALID_FIRST_NAME, regexp = RegexPattern.PERSON_NAME)
    @Size(max = 50, message = ValidationMessage.FIRST_NAME_TOO_LONG)
    String firstName,

    @NotBlank(message = ValidationMessage.BLANK_LAST_NAME)
    @Pattern(message = ValidationMessage.INVALID_LAST_NAME, regexp = RegexPattern.PERSON_NAME)
    @Size(max = 50, message = ValidationMessage.LAST_NAME_TOO_LONG)
    String lastName,

    @NotBlank(message = ValidationMessage.BLANK_EMAIL)
    @Email(message = ValidationMessage.INVALID_EMAIL, regexp = RegexPattern.EMAIL)
    String email,

    @NotBlank(message = ValidationMessage.BLANK_GENDER)
    @EnumValid(enumClass = Gender.class, ignoreCase = true, message = ValidationMessage.INVALID_GENDER)
    String gender
){}
