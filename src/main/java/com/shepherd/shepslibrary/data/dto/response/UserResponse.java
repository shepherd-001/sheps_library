package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.enums.Gender;

public record UserResponse(
    String firstName,
    String lastName,
    String email,
    Gender gender,
    String role,
    boolean enabled,
    boolean emailVerified
){}
