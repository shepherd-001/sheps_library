package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.enums.Gender;

import java.util.UUID;

public record InviteLibrarianResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        Gender gender,
        boolean enabled,
        boolean emailVerified
) {}
