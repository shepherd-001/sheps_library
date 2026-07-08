package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.enums.Gender;

import java.util.UUID;

public record RegisterUserResponse(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        Gender gender,
        boolean enabled,
        boolean emailVerified
) {}
