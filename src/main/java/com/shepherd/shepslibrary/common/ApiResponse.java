package com.shepherd.shepslibrary.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String message;
    private final T data;
    private final Instant timeStamp;
    private final String path;

    public static <T> ApiResponse<T> buildResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .timeStamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> buildResponse(String message){
        return ApiResponse.<T>builder()
                .message(message)
                .timeStamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> buildResponse(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .timeStamp(Instant.now())
                .build();
    }
}
