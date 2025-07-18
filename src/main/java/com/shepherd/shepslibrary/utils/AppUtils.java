package com.shepherd.shepslibrary.utils;

import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public final class AppUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String SORT_BY_CREATED_AT = "createdAt";
    public static final String SORT_DIRECTION_ASC = "ASC";
    public static final int MAX_ISBN_ATTEMPTS = 5;
    public static final int MAX_BORROW_MONTHS = 2;


    public static User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser)) {
                throw new ShepsLibraryException("No authenticated user found");
            }
            return ((AuthenticatedUser) authentication.getPrincipal()).getUser();
        } catch (Exception e) {
            log.error("Error fetching authenticated user: {}", e.getMessage());
            throw new ShepsLibraryException("Failed to fetch authenticated user");
        }
    }

    public static Pageable createPageRequest(int pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        final int MAX_PAGE_SIZE = 100;
        pageNumber = Math.max(pageNumber - 1, 0);
        int resolvedPageSize = (pageSize != null && pageSize > 0)
                ? Math.min(pageSize, MAX_PAGE_SIZE)
                : DEFAULT_PAGE_SIZE;

        String resolvedSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : SORT_BY_CREATED_AT;
        Sort.Direction resolvedDirection = SORT_DIRECTION_ASC.equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(pageNumber, resolvedPageSize, Sort.by(resolvedDirection, resolvedSortBy));
    }

    public static String customAuthResponse(String message, LocalDateTime timestamp, boolean isSuccessful){
        String formattedTimestamp =
                timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss, dd-MM-yyyy"));
        return String.format("{\"message\": \"%s\", \"timestamp\": \"%s\", \"isSuccessful\": \"%b\"}", message, formattedTimestamp, isSuccessful);
    }

    public static String generateISBN() {
        StringBuilder isbn = new StringBuilder(13);
        int checksum = 0;

        isbn.append("97");
        int prefix = SECURE_RANDOM.nextBoolean() ? 8 : 9;
        isbn.append(prefix);
        checksum += 9 + (7 * 3) + prefix;

        for (int i = 3; i < 12; i++) {
            int digit = SECURE_RANDOM.nextInt(10);
            isbn.append(digit);
            checksum += digit * ((i & 1) == 0 ? 1 : 3);
        }

        int checkDigit = (10 - (checksum % 10)) % 10;
        isbn.append(checkDigit);

        return isbn.toString();
    }

    private AppUtils() {
        throw new UnsupportedOperationException(ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS);
    }
}