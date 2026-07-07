package com.shepherd.shepslibrary.common.exceptions.handler;

import com.shepherd.shepslibrary.common.ErrorCode;
import com.shepherd.shepslibrary.common.exceptions.*;
import com.shepherd.shepslibrary.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaught(Exception ex, HttpServletRequest request) {
        log.error("==>> Uncaught exception: {}", ex.getMessage());
//        log.error("==>> Uncaught exception", ex);
        String message = "An unexpected error occurred. Please try again later.";
        return ResponseEntity.internalServerError().body(ErrorResponse.internalServerError(message, request.getRequestURI()));
    }

    @ExceptionHandler({
            BaseApiException.class,
            ShepsLibraryException.class,
            EmailValidationException.class,
            ShepsTokenException.class,
            MailSenderException.class,
            UserAlreadyEnabledException.class,
            AlreadyExistsException.class,
            ResourceNotFoundException.class,
            TransactionException.class,
            UnauthorizedException.class,
            UserNotVerifiedException.class,
            ReservationException.class,
    })
    public ResponseEntity<ErrorResponse> handleBaseApiExceptions(BaseApiException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus()).body(ErrorResponse.of(ex.getMessage(), ex.getErrorCode(), request.getRequestURI()));
    }

    @ExceptionHandler({
            IllegalStateException.class,
            IllegalArgumentException.class,
    })
    public ResponseEntity<ErrorResponse> handleIllegalState(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(ErrorResponse.of(ex.getMessage(), ErrorCode.VALIDATION_ERROR, request.getRequestURI()));
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            DisabledException.class,
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.unauthorized(ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(AuthorizationDeniedException ex, HttpServletRequest request) {
        log.error("Authorization denied exception: {}", ex.getMessage());
        String message = "You are not authorized to access this resource";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.unauthorized(message, request.getRequestURI()));
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            UnsupportedClassVersionError.class
    })
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ErrorResponse.of(ex.getMessage(), ErrorCode.SHEP_LIBRARY_ERROR, request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("==>> Access Denied Exception: {}", ex.getMessage());
        String message = "You do not have the permission to access this resource";
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.forbidden(message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ErrorResponse.validationError(errors, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String parameterName = ex.getParameter().getParameterName();
    String message = String.format("Invalid value '%s' for parameter '%s'. Expected a valid %s format.",
                                   ex.getValue(),
                                   parameterName,
                                   ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        return ResponseEntity.badRequest().body(ErrorResponse.of(message, ErrorCode.INVALID_PARAMETER, request.getRequestURI()));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request) {
        String parameterName = ex.getParameter().getParameterName();
        String message = String.format("Parameter '%s' is required", parameterName);
        return ResponseEntity.badRequest().body(ErrorResponse.of(message, ErrorCode.MISSING_PATH_VARIABLE, request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleException(ConstraintViolationException ex, HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElse("Field validation error");
        log.error("Constraint violation exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.of(message, ErrorCode.BUSINESS_RULE_VIOLATION, request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Invalid value provided for one or more request fields";
        log.error("==>> Http message not readable exception {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ErrorResponse.of(message, ErrorCode.SHEP_LIBRARY_ERROR, request.getRequestURI()));
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(PasswordValidationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());
        return ResponseEntity.status(status).body(ErrorResponse.validationError(ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleException(SecurityException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.forbidden(ex.getMessage(), request.getRequestURI()));
    }
}