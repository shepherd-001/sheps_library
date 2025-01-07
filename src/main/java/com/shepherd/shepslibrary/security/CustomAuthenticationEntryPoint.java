package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.utils.AppUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull AuthenticationException authException) throws IOException {

        log.error("::::: Unauthorized access attempt: {} :::::", authException.getMessage());


        if(!response.isCommitted())
            prepareUnauthorizedResponse(response, authException);
        else log.warn("::::: Unauthorized request received, but response has already been committed. :::::");
    }

    private void prepareUnauthorizedResponse(HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try(PrintWriter writer = response.getWriter()){
            writer.write(AppUtils.customAuthResponse(authException.getMessage(), LocalDateTime.now(), false));
        }
    }
}
