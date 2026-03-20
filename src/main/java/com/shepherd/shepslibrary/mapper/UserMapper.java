package com.shepherd.shepslibrary.mapper;

import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.request.RegisterUserRequest;
import com.shepherd.shepslibrary.data.dto.response.*;
import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.data.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = CentralConfig.class)
public interface UserMapper {
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trim")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trim")
    @Mapping(target = "email", source = "email", qualifiedByName = "toLowerCaseTrim")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "toGender")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User mapToUser(RegisterUserRequest request);

    @Mapping(target = "userId", source = "user.id")
    RegisterUserResponse mapToRegisterResponse(User user);

    EmailVerificationResponse mapToEmailVerificationResponse(User user, AuthResponse authResponse);

    @Mapping(target = "role", source = "role.name")
    UserResponse mapToUserResponse(User user);


    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trim")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trim")
    @Mapping(target = "email", source = "email", qualifiedByName = "toLowerCaseTrim")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "toGender")
    @Mapping(target = "role", ignore = true)
    User mapToLibrarian(InviteLibrarianRequest request);

    InviteLibrarianResponse mapToInviteLibrarianResponse(User user);

    @Named("toLowerCaseTrim")
    default String toLowerCaseTrim(String value) {
        return value != null ? value.toLowerCase().trim() : null;
    }

    @Named("trim")
    default String trim(String value) {
        return value != null ? value.trim() : null;
    }

    @Named("toGender")
    default Gender toGender(String value) {
        return value == null ? null : Gender.valueOf(value.trim().toUpperCase());
    }
}