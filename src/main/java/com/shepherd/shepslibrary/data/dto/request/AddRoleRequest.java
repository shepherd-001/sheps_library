package com.shepherd.shepslibrary.data.dto.request;

import jakarta.validation.constraints.*;

public record AddRoleRequest(
        @NotBlank @Size(max = 50) @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]{0,49}") String name,
        @NotBlank @Size(max = 500) String reason
) {}
