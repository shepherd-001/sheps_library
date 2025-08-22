package com.shepherd.shepslibrary.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String message;
    private final T data;
    private final boolean isSuccessful;
    private final Instant timeStamp;

    public static <T> ApiResponse<T> buildResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .isSuccessful(true)
                .timeStamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> buildResponse(String message){
        return ApiResponse.<T>builder()
                .message(message)
                .isSuccessful(true)
                .timeStamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> buildResponse(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .isSuccessful(true)
                .timeStamp(Instant.now())
                .build();
    }
}
