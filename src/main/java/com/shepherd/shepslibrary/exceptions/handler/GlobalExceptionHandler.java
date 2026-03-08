package com.shepherd.shepslibrary.exceptions.handler;

import com.shepherd.shepslibrary.common.ApiResponse;
import com.shepherd.shepslibrary.exceptions.*;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex, HttpServletRequest request){
        log.error("==>> Uncaught exception: {}", ex.getMessage());
        String message = "An unexpected error occurred. Please try again later.";
        return ResponseEntity.internalServerError().body(ApiResponse.error(message, request));
    }

    @ExceptionHandler({
            ShepsLibraryException.class,
            IllegalStateException.class,
            IllegalArgumentException.class,
            EmailValidationException.class,
            ShepsTokenException.class,
            MailSenderException.class,
            UserAlreadyEnabledException.class,
            TransactionException.class,
            ReservationException.class,

    })
    public ResponseEntity<ApiResponse<?>> handleBadRequest(Exception ex, HttpServletRequest request){
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage(), request));
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            UnauthorizedException.class,
            UserNotVerifiedException.class,
            BadCredentialsException.class,
            DisabledException.class,
    })
    public ResponseEntity<ApiResponse<?>> handleUnauthorized(Exception ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage(), request));
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            UnsupportedClassVersionError.class
    })
    public ResponseEntity<ApiResponse<?>> handleMethodNotAllowed(Exception ex, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponse.error(ex.getMessage(), request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleException(AccessDeniedException ex, HttpServletRequest request){
        log.error("==>> Access Denied Exception: {}", ex.getMessage());
        String message = "You do not have the permission to access this resource";
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(message, request));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleException(AuthorizationDeniedException ex, HttpServletRequest request){
        log.error("Authorization denied exception: {}", ex.getMessage());
        String message = "You are not authorized to access this resource";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(message, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ApiResponse.error(errors, request));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleEnumConversionError(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid value for parameter '%s': %s".formatted(ex.getName(), ex.getValue());
        return ResponseEntity.badRequest().body(ApiResponse.error(message, request));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleException(ConstraintViolationException ex, HttpServletRequest request) {
        String errorMessage = ex.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElse("Field validation error");
        log.error("Constraint violation exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage, request));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleException(AlreadyExistsException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage(), request));
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleException(PasswordValidationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());
        return ResponseEntity.status(status).body(ApiResponse.error(ex.getMessage(), request));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonParseError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("==>> Http message not readable exception {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid value provided for one or more request fields", request));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleException(ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage(), request));
    }
}