package com.shepherd.shepslibrary.data.dto.request;

import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddRoleRequest {
    @NotBlank(message = ValidationMessage.BLANK_ROLE)
    @Pattern(message = ValidationMessage.INVALID_ROLE, regexp = RegexPattern.ROLE)
    @Size(max = 100, message = ValidationMessage.ROLE_NAME_TOO_LONG)
    private String name;

    @Size(max = 255, message = ValidationMessage.ROLE_DESCRIPTION_TOO_LONG)
    private String description;
}
