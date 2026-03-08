package com.shepherd.shepslibrary.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shepherd.shepslibrary.common.ApiResponse;
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

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ERROR_MESSAGE = "Authentication required. Please log in";

    @Override
    public void commence(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull AuthenticationException authException) throws IOException {

        String requestURI = request.getRequestURI();

        if(response.isCommitted()){
            log.warn("Response already commited for unauthorized request: {}", requestURI);
            return;
        }

        log.warn("Unauthorized access attempt: {} | Reason: {}", requestURI, authException.getMessage());

        prepareUnauthorizedResponse(request, response);
    }

    private void prepareUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> errorResponse = ApiResponse.error(ERROR_MESSAGE, request);

        try (PrintWriter writer = response.getWriter()) {
            objectMapper.writeValue(writer, errorResponse);
        } catch (Exception ex) {
            log.error("Failed to write unauthorized response", ex);
        }
    }
}