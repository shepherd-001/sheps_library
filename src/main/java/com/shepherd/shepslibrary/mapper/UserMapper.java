package com.shepherd.shepslibrary.mapper;

import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.*;
import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.data.model.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(config = CentralConfig.class)
public interface UserMapper {
    @Mapping(target = "firstName", expression = "java(request.getFirstName().trim())")
    @Mapping(target = "lastName", expression = "java(request.getLastName().trim())")
    @Mapping(target = "email", expression = "java(request.getEmail().toLowerCase().trim())")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    @Mapping(target = "gender", expression = "java(toGender(request.getGender()))")
//    @Mapping(target = "role", constant = "MEMBER")
    User mapToUser(RegisterUserRequest request, @Context PasswordEncoder passwordEncoder);

    default Gender toGender(String value) {
        return value == null ? null : Gender.valueOf(value.trim().toUpperCase());
    }


    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "isEnabled", source = "enabled")
    @Mapping(target = "isRevoked", source = "revoked")
    RegisterUserResponse mapToRegisterResponse(User user);


    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "isEnabled", source = "user.enabled")
    @Mapping(target = "accessToken", source = "authResponse.accessToken")
    @Mapping(target = "refreshToken", source = "authResponse.refreshToken")
    EmailConfirmationResponse mapToEmailConfirmationResponse(User user, AuthResponse authResponse);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "isEnabled", source = "enabled")
    @Mapping(target = "isRevoked", source = "revoked")
    @Mapping(target = "role", ignore = true)
    UserResponse mapToUserResponse(User user);


    @Mapping(target = "firstName", expression = "java(request.getFirstName().trim())")
    @Mapping(target = "lastName", expression = "java(request.getLastName().trim())")
    @Mapping(target = "email", expression = "java(request.getEmail().toLowerCase().trim())")
    @Mapping(target = "gender", expression = "java(toGender(request.getGender()))")
    @Mapping(target = "role", ignore = true)
    User mapToLibrarian(InviteLibrarianRequest request);

    @Mapping(target = "librarianId", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "librarianEmail", source = "email")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "isEnabled", source = "enabled")
    @Mapping(target = "isRevoked", source = "revoked")
    InviteLibrarianResponse mapToInviteLibrarianResponse(User user);
}