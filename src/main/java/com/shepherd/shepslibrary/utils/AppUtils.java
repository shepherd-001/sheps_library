package com.shepherd.shepslibrary.utils;

import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.security.SecureRandom;
import java.util.Set;

@Slf4j
public final class AppUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String SORT_BY_CREATED_AT = "createdAt";
    public static final String SORT_DIRECTION_ASC = "ASC";
    public static final int MAX_ISBN_ATTEMPTS = 5;
    public static final int MAX_BORROW_MONTHS = 2;


    public static Pageable createPageRequest(PaginationRequest paginationRequest, Set<String> allowedSortFields) {

        String sortBy = resolvedSortBy(paginationRequest.getSort(), allowedSortFields);
        return PageRequest.of(resolvedPageNumber(paginationRequest.getPage()),
                resolvedPageSize(paginationRequest.getSize()),
                Sort.by(resolvedSortDirection(paginationRequest.getDirection()), sortBy));
    }

    public static int resolvedPageNumber(int pageNumber) {
        return Math.max(pageNumber - 1, 0);
    }

    public static int resolvedPageSize(int pageSize) {
        return (pageSize > 0)
                ? Math.min(pageSize, MAX_PAGE_SIZE)
                : DEFAULT_PAGE_SIZE;
    }

//    public static String resolvedSortBy(String sortBy, Set<String> allowedSortFields){
//        if(sortBy == null || sortBy.isBlank() || allowedSortFields == null
//                || allowedSortFields.isEmpty())
//            return SORT_BY_CREATED_AT;
//        String trimmed = sortBy.trim();
//        return allowedSortFields.contains(trimmed)
//                ? trimmed
//                : SORT_BY_CREATED_AT;
//    }

    public static String resolvedSortBy(String sortBy, Set<String> allowedSortFields) {
        if (sortBy == null || sortBy.isBlank() || allowedSortFields == null
                || allowedSortFields.isEmpty())
            return SORT_BY_CREATED_AT;
        String trimmed = sortBy.trim();
        return allowedSortFields.stream()
                .filter(field -> field.equalsIgnoreCase(trimmed))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("==>> Invalid sortBy '{}' received. Falling back to '{}'", trimmed, SORT_BY_CREATED_AT);
                    return SORT_BY_CREATED_AT;
                });
    }

    public static Sort.Direction resolvedSortDirection(String sortDirection) {
        return SORT_DIRECTION_ASC.equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
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

//    public static void validateID(UUID id, String errorMessage){
//
//    }

//    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
//
//public static String generateOtp() {
//    int number = SECURE_RANDOM.nextInt(900_000) + 100_000; // 100000-999999
//    return String.valueOf(number);
//}


    private AppUtils() {
        throw new UnsupportedOperationException(ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS);
    }
}