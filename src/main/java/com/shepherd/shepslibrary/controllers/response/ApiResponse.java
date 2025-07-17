package com.shepherd.shepslibrary.controllers.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean isSuccessful;
    private Instant timeStamp;

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
