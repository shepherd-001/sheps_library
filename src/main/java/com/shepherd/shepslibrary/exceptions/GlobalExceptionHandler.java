package com.shepherd.shepslibrary.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public ResponseEntity<ApiError> handleException(Exception ex){
        log.error("Exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ShepsLibraryException.class)
    public ResponseEntity<ApiError> handleException(ShepsLibraryException ex){
        log.error("Sheps library exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleException(UsernameNotFoundException ex){
        log.error("User name not found exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ApiError> handleException(UserNotVerifiedException ex){
        log.error("User not verified exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleException(BadCredentialsException ex){
        log.error("Bad credentials exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleException(AuthorizationDeniedException ex){
        log.error("Authorization denied exception: {}", ex.getMessage());
        String errorMessage = "You are not permitted to access this resource";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.buildErrorResponse(errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ApiError.buildErrorResponse(errors));
//        String errorMessage = ex.getBindingResult().getFieldErrors()
//                .stream()
//                .findFirst()
//                .map(FieldError::getDefaultMessage)
//                .orElse("Field validation error");
//
//        log.error("::::: Method argument not valid exception: {} :::::", ex.getMessage());
//        return new ResponseEntity<>(ApiError.buildErrorResponse(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleEnumConversionError(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value for parameter '%s': %s".formatted(ex.getName(), ex.getValue());
        return ResponseEntity.badRequest().body(ApiError.buildErrorResponse(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Field validation error");

        log.error("Constraint violation exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiError> handleException(UnsupportedOperationException ex){
        log.error("Unsupported operation exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AlreadyExistsException.class)

    public ResponseEntity<ApiError> handleException(AlreadyExistsException ex){
        log.error("Already exists exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailValidationException.class)
    public ResponseEntity<ApiError> handleException(EmailValidationException ex){
        log.error("Email validation exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<ApiError> handleException(PasswordValidationException ex) {
        log.error("Password validation exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(MailSenderException.class)
    public ResponseEntity<ApiError> handleException(MailSenderException ex) {
        log.error("Mail sender exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShepsTokenException.class)
    public ResponseEntity<ApiError> handleException(ShepsTokenException ex) {
        log.error("Sheps token exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyEnabledException.class)
    public ResponseEntity<ApiError> handleException(UserAlreadyEnabledException ex) {
        log.error("User already enable exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException ex) {
        log.error("Resource not found exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ApiError> handleException(TransactionException ex) {
        log.error("Transaction exception: {}", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ApiError> handleException(ReservationException ex) {
        log.error("Reservation exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
