package com.shepherd.shepslibrary.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String message,
        T data,
        Instant timestamp,
        boolean success
) {
    public static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse<>(message, data, Instant.now(), true);
    }

    public static <T> ApiResponse<T> of(String message) {
        return of(message, null);
    }

    public static <T> ApiResponse<T> of(T data) {
        return of(null, data);
    }
}