package com.shepherd.shepslibrary.exceptions.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final String message;
    private final Instant timestamp;
    private final boolean status;
    private final Object error;

    public static ApiError buildResponse(String message){
        return ApiError.builder()
                .message(message)
                .timestamp(Instant.now())
                .status(false)
                .build();
    }

    public static ApiError buildResponse(Object error){
        return ApiError.builder()
                .error(error)
                .timestamp(Instant.now())
                .status(false)
                .build();
    }
}