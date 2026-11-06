package com.shepherd.shepslibrary.data.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

/** Full replacement; an empty list deliberately revokes all permissions. */
public record AssignPermissionRequest(
        @NotBlank @Size(max = 50) @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]{0,49}") String roleName,
        @NotNull @Size(max = 100) List<@NotBlank @Size(max = 150) String> permissionNames,
        @NotNull @PositiveOrZero Long expectedVersion,
        @NotBlank @Size(max = 500) String reason
) {}
