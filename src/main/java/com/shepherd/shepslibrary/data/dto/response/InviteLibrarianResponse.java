package com.shepherd.shepslibrary.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shepherd.shepslibrary.data.model.Gender;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteLibrarianResponse {
    private String message;
    private UUID librarianId;
    private String librarianEmail;
    private String firstName;
    private String lastName;
    private Gender gender;
    private boolean isEnabled;
    private boolean isRevoked;
}
