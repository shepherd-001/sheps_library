package com.shepherd.shepslibrary.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final String message;
    @JsonFormat(pattern = "HH:mm:ss, dd-MM-yyyy")
    private final LocalDateTime timestamp;
    private final boolean status;
    private final Object errors;

    public static ApiError buildErrorResponse(String message){
        return ApiError.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(false)
                .build();
    }

    public static ApiError buildErrorResponse(Object error){
        return ApiError.builder()
                .errors(error)
                .timestamp(LocalDateTime.now())
                .status(false)
                .build();
    }


}
