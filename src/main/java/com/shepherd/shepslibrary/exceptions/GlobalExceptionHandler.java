package com.shepherd.shepslibrary.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex){
        log.error("::::: Exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShepsLibraryException.class)
    public ResponseEntity<ApiError> handleException(ShepsLibraryException ex){
        log.error("::::: Sheps library exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            log.error("::::: Method argument not valid exception: {} :::::", ex.getMessage());
        }
        return new ResponseEntity<>(ApiError.buildErrorResponse(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiError> handleException(UnsupportedOperationException ex){
        log.error("::::: Unsupported operation exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiError> handleException(AlreadyExistsException ex){
        log.error("::::: Already exists exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailValidationException.class)
    public ResponseEntity<ApiError> handleException(EmailValidationException ex){
        log.error("::::: Email validation exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<ApiError> handleException(PasswordValidationException ex) {
        log.error("::::: Password validation exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(MailSenderException.class)
    public ResponseEntity<ApiError> handleException(MailSenderException ex) {
        log.error("::::: Mail sender exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShepsTokenException.class)
    public ResponseEntity<ApiError> handleException(ShepsTokenException ex) {
        log.error("::::: Sheps token exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyEnabledException.class)
    public ResponseEntity<ApiError> handleException(UserAlreadyEnabledException ex) {
        log.error("::::: User already enable exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException ex) {
        log.error("::::: Resource not found exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ApiError> handleException(TransactionException ex) {
        log.error("::::: Transaction exception: {} :::::", ex.getMessage());
        return new ResponseEntity<>(ApiError.buildErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
