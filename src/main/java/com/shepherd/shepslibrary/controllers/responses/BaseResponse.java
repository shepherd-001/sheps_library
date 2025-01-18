package com.shepherd.shepslibrary.controllers.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private T data;
    private String message;
    private boolean isSuccessful;
    @JsonFormat(pattern = "HH:mm:ss, dd-MM-yyyy")
    private LocalDateTime timeStamp;


    public static BaseResponse<Object> buildResponse(Object data){
        return BaseResponse.builder()
                .data(data)
                .isSuccessful(true)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static BaseResponse<Object> buildResponse(String message){
        return BaseResponse.builder()
                .message(message)
                .isSuccessful(true)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
