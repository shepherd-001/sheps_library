package com.shepherd.shepslibrary.common.response;

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
    private final T errors;
    private final Instant timeStamp;
    private final String path;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .timeStamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message){
        return ApiResponse.<T>builder()
                .message(message)
                .timeStamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .data(data)
                .timeStamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T errors, HttpServletRequest request) {
        return ApiResponse.<T>builder()
                .message(message)
                .errors(errors)
                .timeStamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpServletRequest request) {
        return ApiResponse.<T>builder()
                .message(message)
                .timeStamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }

    public static <T> ApiResponse<T> error(T errors, HttpServletRequest request) {
        return ApiResponse.<T>builder()
                .errors(errors)
                .timeStamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }
}
