package com.shepherd.shepslibrary.service.emailValidator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shepherd.shepslibrary.exceptions.EmailValidationException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailValidationServiceImpl implements EmailValidationService{
    @Value("${zero_bounce_api_key}")
    private String apiKey;
    @Value("${zero_bounce_url}")
    private String validationUrl;
    private final WebClient webClient;


    private static final Set<String> DISPOSABLE_EMAIL_DOMAINS = Set.of(
            "10minutemail.com", "guerrillamail.com", "mailinator.com",
            "temp-mail.org", "throwawaymail.com", "fakemailgenerator.com",
            "mohmal.com", "getnada.com", "yopmail.com", "disposablemail.com",
            "maildrop.cc", "tempmailo.com", "pookmail.com", "emailondeck.com",
            "trashmail.com", "mailcatch.com", "tempmail.net", "fakeinbox.com",
            "jetable.org", "spamgourmet.com", "mytemp.email", "spambox.us",
            "tempmailaddress.com", "mailcatcher.com", "tempinbox.com",
            "tempmail.us", "dumpmail.de", "spamthis.co", "getairmail.com",
            "dispostable.com", "throwawaymail.net", "boun.cr", "on0.com",
            "dropmail.me", "safepostmail.com", "mailnesia.com",
            "randommail.org", "guerrillamail.net", "fakeemailgenerator.com"
    );

    @Override
    public void checkAndValidateEmail(String email) {
        if(!isValidEmail(email))
            throw new EmailValidationException("Your email address is not acceptable");
    }

    private boolean isValidEmail(String email) {
        email = email.toLowerCase().trim();
        log.info("Initiating email validation for: {}", email);

        checkEmailNotBlank(email);
        validateNonDisposableEmail(email);

        return validateEmailWithExternalService(email);
    }

    private boolean validateEmailWithExternalService(String email) {
        String url = buildValidationUrl(email);

        try {
            EmailValidationResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(EmailValidationResponse.class)
                    .block();

            return isEmailValidAndAllowed(Objects.requireNonNull(response));

        } catch (WebClientResponseException ex) {
            log.error("Error during email validation: {}", ex.getMessage());
            throw new EmailValidationException("Email validation failed. Please try again.");
        }
    }

    private static boolean isEmailValidAndAllowed(EmailValidationResponse response) {
        String status = Optional.ofNullable(response.getStatus()).map(String::toLowerCase).orElse("");
        String subStatus = Optional.ofNullable(response.getSubStatus()).map(String::toLowerCase).orElse("");

        return switch (status) {
            case "valid" -> {
                log.info("Valid email address -> sub-status: {}", subStatus);
                yield true;
            }
            case "do_not_mail" -> handleDoNotMailSubStatus(subStatus);
            default -> {
                log.warn("Invalid email address -> sub-status: {}", subStatus);
                throw new EmailValidationException("Invalid email address. Please use a valid one.");
            }
        };
    }

    private static boolean handleDoNotMailSubStatus(String subStatus) {
        return switch (subStatus) {
            case "role_based", "role_based_catch_all" -> {
                log.info("Valid email with 'do_not_mail' status -> sub-status: {}", subStatus);
                yield true;
            }
            case "disposable" -> {
                log.error("Disposable email detected.");
                throw new EmailValidationException("Disposable email addresses are not allowed.");
            }
            default -> {
                log.error("Unacceptable email with sub-status: {}", subStatus);
                throw new EmailValidationException("Unacceptable email address. Please use a valid one.");
            }
        };
    }

    private static void checkEmailNotBlank(String email) {
        if (email.isBlank()) {
            throw new EmailValidationException("Email address cannot be blank.");
        }
    }

    private static void validateNonDisposableEmail(String email) {
        String domain = extractDomainFromEmail(email);
        if (DISPOSABLE_EMAIL_DOMAINS.contains(domain)) {
            log.error("Disposable email detected: {}", email);
            throw new EmailValidationException("Disposable email addresses are not allowed.");
        }
    }

    private static String extractDomainFromEmail(String email) {
        return email.substring(email.indexOf('@') + 1);
    }

    private String buildValidationUrl(String email) {
        return UriComponentsBuilder.fromUriString(validationUrl)
                .queryParam("api_key", apiKey)
                .queryParam("email", email)
                .toUriString();
    }

    @Builder
    @Getter
    private static class EmailValidationResponse {
        private String address;
        private String status;
        @JsonProperty("sub_status")
        private String subStatus;
        @JsonProperty("free_email")
        private boolean freeEmail;
        @JsonProperty("did_you_mean")
        private String didYouMean;
        private String account;
        private String domain;
        @JsonProperty("domain_age_days")
        private String domainAgeDays;
        @JsonProperty("active_in_days")
        private String activeInDays;
        @JsonProperty("smtp_provider")
        private String smtpProvider;
        @JsonProperty("mx_record")
        private String mxRecord;
        @JsonProperty("mx_found")
        private boolean mxFound;
        private String firstname;
        private String lastname;
        private String gender;
        private String country;
        private String region;
        private String city;
        private String zipcode;
        @JsonProperty("processed_at")
        private String processedAt;
    }
}

