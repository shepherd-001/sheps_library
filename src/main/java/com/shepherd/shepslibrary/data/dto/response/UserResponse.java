package com.shepherd.shepslibrary.data.dto.response;

import com.shepherd.shepslibrary.data.model.Gender;
import lombok.Builder;
import lombok.Getter;

public record UserResponse(
    String firstName,
    String lastName,
    String email,
    Gender gender,
    String role,
    boolean enabled,
    boolean emailVerified
){}
