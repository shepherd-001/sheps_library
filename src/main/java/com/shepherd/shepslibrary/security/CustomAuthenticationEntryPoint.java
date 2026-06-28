package com.shepherd.shepslibrary.security;

import com.shepherd.shepslibrary.common.response.ErrorResponse;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private static final String ERROR_MESSAGE = "Authentication required. Please log in";

    @Override
    public void commence(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull AuthenticationException authException) {

        String requestURI = request.getRequestURI();

        if (response.isCommitted()) {
            log.warn("==>> Response already commited for unauthorized request: {}", requestURI);
            return;
        }

        log.warn("==>> Unauthorized access attempt: {} | Reason: {}", requestURI, authException.getMessage());

        prepareUnauthorizedResponse(request, response);
    }

    private void prepareUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.unauthorized(ERROR_MESSAGE, request.getRequestURI());

        try (PrintWriter writer = response.getWriter()) {
            objectMapper.writeValue(writer, errorResponse);
        } catch (Exception ex) {
            log.error("==>> Failed to write unauthorized response", ex);
        }
    }
}