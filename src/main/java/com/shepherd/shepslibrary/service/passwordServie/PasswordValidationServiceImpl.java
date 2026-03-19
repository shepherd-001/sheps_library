//package com.shepherd.shepslibrary.service.passwordServie;
//
//import com.shepherd.shepslibrary.exceptions.PasswordValidationException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//import java.time.Duration;
//
//import static org.springframework.http.HttpStatus.BAD_REQUEST;
//
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class PasswordValidationServiceImpl implements PasswordValidationService{
//    private final WebClient.Builder webClientBuilder;
//    @Value("${have_i_been_pawned_url}")
//    private String haveIBeenPawnedUrl;
//    private static final int SHA1_PREFIX_LENGTH = 5;
//    private static final Duration API_TIMEOUT = Duration.ofSeconds(5);
//    private static final String USER_AGENT = "ShepsLibrary/1.0";
//
//
//    @Override
//    public void validatePasswordNotBreached(String password) {
//        if(isPasswordBreached(password))
//            throw new PasswordValidationException("This password has been compromised. Use a new, unique password"
//                    , BAD_REQUEST.value());
//    }
//
//    private boolean isPasswordBreached(String password) {
//        validatePasswordNotBlank(password);
//
//        String sha1Hash = DigestUtils.sha1Hex(password).toUpperCase();
//        String prefix = sha1Hash.substring(0, SHA1_PREFIX_LENGTH);
//        String suffix = sha1Hash.substring(SHA1_PREFIX_LENGTH);
//
//        String apiUrl = String.format("%s/%s", haveIBeenPawnedUrl, prefix);
//        log.info("Checking password breach for prefix: {}", prefix);
//
//        try {
//            String webClientResponse = webClientBuilder
//                    .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
//                    .build()
//                    .get()
//                    .uri(apiUrl)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, response -> {
//                        throw new PasswordValidationException(getErrorMessage(response.statusCode()), response.statusCode().value());
//                    })
//                    .bodyToMono(String.class)
//                    .timeout(API_TIMEOUT)
//                    .block();
//
//            return webClientResponse != null && webClientResponse.contains(suffix);
//
//        } catch (WebClientResponseException ex) {
//            log.error("Error occurred during password breach check: {}", ex.getMessage());
//            throw new PasswordValidationException(getErrorMessage(ex.getStatusCode()), ex.getStatusCode().value());
//        } catch (Exception ex) {
//                log.error("Unexpected error occurred during password breach check: {}", ex.getMessage());
//                throw new PasswordValidationException("An unexpected error occurred during password validation", 500);
//            }
//                    }
//
//    private void validatePasswordNotBlank(String password) {
//        if (password == null || password.isBlank()) {
//            throw new PasswordValidationException("Password is required to proceed with validation", 400);
//        }
//    }
//
//    private String getErrorMessage(HttpStatusCode statusCode) {
//        return switch (HttpStatus.valueOf(statusCode.value())) {
//            case BAD_REQUEST -> "The password format is invalid";
//            case UNAUTHORIZED -> "API key is missing or invalid";
//            case FORBIDDEN -> "User-Agent header is missing in the request";
//            case NOT_FOUND -> "Password not breached";
//            case TOO_MANY_REQUESTS -> "Rate limit exceeded. Please try again later";
//            case SERVICE_UNAVAILABLE -> "Password validation service is temporarily unavailable. Please try again later";
//            default -> String.format("Unexpected error when validating password. Code %s", statusCode.value());
//        };
//    }
//}
