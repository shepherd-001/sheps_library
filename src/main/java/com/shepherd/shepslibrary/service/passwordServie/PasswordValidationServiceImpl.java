package com.shepherd.shepslibrary.service.passwordServie;

import com.shepherd.shepslibrary.exceptions.PasswordValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordValidationServiceImpl implements PasswordValidationService{
    private final RestTemplate restTemplate;
    @Value("${have_i_been_pawned_url}")
    private String haveIBeenPawnedUrl;
    private static final int SHA1_PREFIX_LENGTH = 5;
    private static final int SHA1_SUFFIX_START = 5;


    @Override
    public boolean isPasswordBreached(String password) {
        checkPasswordNotBlank(password);
        String sha1Hash = DigestUtils.sha1Hex(password).toUpperCase();
        String prefix = sha1Hash.substring(0, SHA1_PREFIX_LENGTH);
        String suffix = sha1Hash.substring(SHA1_SUFFIX_START);

        String apiUrl = String.format("%s/%s", haveIBeenPawnedUrl, prefix);
        try{
            log.info("::::: Checking password breach :::::");
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null){
                return response.getBody().contains(suffix);
            }
        }catch (HttpClientErrorException ex){
            log.error("::::: Error occurred during password breach check: {} :::::", ex.getMessage());
            handleHttpError(ex);
        }
        return false;
    }

    private void checkPasswordNotBlank(String password) {
        if(password.isBlank())
            throw new PasswordValidationException("Password is required to proceed validation", 400);
    }

    private void handleHttpError(HttpClientErrorException ex) {
        HttpStatus statusCode = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorMessage = getErrorMessage(statusCode);
        throw new PasswordValidationException(errorMessage, statusCode.value());
    }

    private String getErrorMessage(HttpStatus statusCode) {
        return switch (statusCode) {
            case BAD_REQUEST -> "The password format is invalid";
            case UNAUTHORIZED -> "API key is missing or invalid";
            case FORBIDDEN -> "User-Agent header is missing in the request";
            case NOT_FOUND -> "Password not breached";
            case TOO_MANY_REQUESTS -> "Rate limit exceeded. Please try again later";
            case SERVICE_UNAVAILABLE -> "Password validation service is temporarily unavailable. Please try again later";
            default -> String.format("Client error exception when validating password. Code %s", statusCode.value());
        };
    }
}
