package com.shepherd.shepslibrary.utils.paginationUtils;

import com.shepherd.shepslibrary.utils.ErrorMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public final class PageUtils {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_PAGE_NUMBER = 1_000_000;
    public static final String SORT_BY_CREATED_AT = "createdAt";


    public static Pageable createPageRequest(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be at least 1");
        }
        if (pageNumber > MAX_PAGE_NUMBER) {
            throw new IllegalArgumentException("Page number exceeds maximum allowed: " + MAX_PAGE_NUMBER);
        }

        int resolvedPageNumber = pageNumber - 1; // 0-based for Spring

        int resolvedPageSize = (pageSize < 1)
                ? DEFAULT_PAGE_SIZE
                : Math.min(pageSize, MAX_PAGE_SIZE);

        String resolvedSortBy = (!sortBy.isBlank())
                ? sortBy.trim()
                : SORT_BY_CREATED_AT;

        Sort.Direction resolvedSortDirection = Optional.ofNullable(sortDirection)
                .filter(s -> !s.isBlank())
                .map(s -> {
                    try {
                        return Sort.Direction.fromString(s.trim());
                    } catch (IllegalArgumentException e) {
                        return Sort.Direction.DESC; // fallback gracefully
                    }
                })
                .orElse(Sort.Direction.DESC);

        return PageRequest.of(resolvedPageNumber, resolvedPageSize, resolvedSortDirection, resolvedSortBy);
    }

    private PageUtils() {
        throw new UnsupportedOperationException(ErrorMessage.NON_INSTANTIABLE_UTILITY_CLASS);
    }
}



//public final class PageUtils {
//    private static final int MAX_PAGE_SIZE = 100;
//    private static final int MAX_PAGE_NUMBER = 1_000_000;
//    private static final int DEFAULT_PAGE_SIZE = 10;
//    public static final String SORT_BY_CREATED_AT = "createdAt";
//    private static final String ERROR_NON_INSTANTIABLE = "Utility class cannot be instantiated";
//
//    // Whitelist of allowed sort columns for different entities
//    private static final Map<String, Set<String>> ALLOWED_SORT_COLUMNS = Map.of(
//        "user", Set.of("id", "username", "email", "createdAt", "updatedAt"),
//        "product", Set.of("id", "name", "price", "createdAt", "updatedAt"),
//        "order", Set.of("id", "userId", "totalAmount", "createdAt", "status")
//    );
//
//    private PageUtils() {
//        throw new UnsupportedOperationException(ERROR_NON_INSTANTIABLE);
//    }
//
//    public static Pageable createPageRequest(int pageNumber, int pageSize,
//                                           String sortBy, String sortDirection) {
//        return createPageRequest(pageNumber, pageSize, sortBy, sortDirection, null);
//    }
//
//    public static Pageable createPageRequest(int pageNumber, int pageSize,
//                                           String sortBy, String sortDirection,
//                                           String entityType) {
//        validatePageNumber(pageNumber);
//        int resolvedPageNumber = pageNumber - 1;
//        int resolvedPageSize = resolvePageSize(pageSize);
//
//        Sort sort = createSort(sortBy, sortDirection, entityType);
//
//        return PageRequest.of(resolvedPageNumber, resolvedPageSize, sort);
//    }
//
//    public static Pageable createPageRequest(int pageNumber, int pageSize,
//                                           List<Sort.Order> sortOrders) {
//        validatePageNumber(pageNumber);
//        int resolvedPageNumber = pageNumber - 1;
//        int resolvedPageSize = resolvePageSize(pageSize);
//
//        Sort sort = sortOrders != null && !sortOrders.isEmpty()
//            ? Sort.by(sortOrders)
//            : Sort.by(Sort.Direction.DESC, SORT_BY_CREATED_AT);
//
//        return PageRequest.of(resolvedPageNumber, resolvedPageSize, sort);
//    }
//
//    private static void validatePageNumber(int pageNumber) {
//        if (pageNumber < 1) {
//            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
//        }
//        if (pageNumber > MAX_PAGE_NUMBER) {
//            throw new IllegalArgumentException("Page number cannot exceed " + MAX_PAGE_NUMBER);
//        }
//    }
//
//    private static int resolvePageSize(int pageSize) {
//        if (pageSize < 1) {
//            return DEFAULT_PAGE_SIZE;
//        }
//        return Math.min(pageSize, MAX_PAGE_SIZE);
//    }
//
//    private static Sort createSort(String sortBy, String sortDirection, String entityType) {
//        String resolvedSortBy = resolveSortBy(sortBy, entityType);
//        Sort.Direction resolvedDirection = resolveSortDirection(sortDirection);
//
//        return Sort.by(resolvedDirection, resolvedSortBy);
//    }
//
//    private static String resolveSortBy(String sortBy, String entityType) {
//        if (sortBy == null || sortBy.isBlank()) {
//            return SORT_BY_CREATED_AT;
//        }
//
//        String trimmedSortBy = sortBy.trim();
//
//        // Validate against SQL injection if entityType is provided
//        if (entityType != null && !entityType.isBlank()) {
//            validateSortColumn(trimmedSortBy, entityType);
//        }
//
//        return trimmedSortBy;
//    }
//
//    private static void validateSortColumn(String columnName, String entityType) {
//        Set<String> allowedColumns = ALLOWED_SORT_COLUMNS.get(entityType.toLowerCase());
//        if (allowedColumns == null) {
//            throw new IllegalArgumentException("Unknown entity type: " + entityType);
//        }
//
//        if (!allowedColumns.contains(columnName.toLowerCase())) {
//            throw new IllegalArgumentException("Invalid sort column '" + columnName +
//                                             "' for entity type: " + entityType);
//        }
//    }
//
//    private static Sort.Direction resolveSortDirection(String sortDirection) {
//        if (sortDirection == null || sortDirection.isBlank()) {
//            return Sort.Direction.DESC;
//        }
//
//        try {
//            return Sort.Direction.fromString(sortDirection.trim());
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid sort direction: " + sortDirection +
//                                             ". Must be 'ASC' or 'DESC'", e);
//        }
//    }
//
//    // Helper method for common multi-sort scenarios
//    public static List<Sort.Order> createSortOrders(String primarySort, String primaryDirection,
//                                                   String secondarySort, String secondaryDirection) {
//        List<Sort.Order> orders = new ArrayList<>();
//
//        if (primarySort != null && !primarySort.isBlank()) {
//            orders.add(new Sort.Order(resolveSortDirection(primaryDirection), primarySort));
//        }
//
//        if (secondarySort != null && !secondarySort.isBlank()) {
//            orders.add(new Sort.Order(resolveSortDirection(secondaryDirection), secondarySort));
//        }
//
//        return orders;
//    }
//
//    // Utility method for frontend compatibility
//    public static Pageable createPageRequestFromZeroBased(int zeroBasedPageNumber, int pageSize,
//                                                        String sortBy, String sortDirection) {
//        return createPageRequest(zeroBasedPageNumber + 1, pageSize, sortBy, sortDirection);
//    }
//}



//private static Optional<Sort.Direction> parseSortDirection(String sortDirection) {
//        return Optional.ofNullable(sortDirection)
//                .filter(StringUtils::hasText)
//                .map(String::trim)
//                .map(s -> {
//                    try {
//                        return Sort.Direction.fromString(s);
//                    } catch (IllegalArgumentException e) {
//                        LOGGER.warn("Invalid sort direction '{}', falling back to DESC", s);
//                        return Sort.Direction.DESC;
//                    }
//                });
//    }
//
//    private static Sort buildSort(String sortBy, Sort.Direction direction, Set<String> allowedSortFields) {
//        String trimmedSortBy = Optional.ofNullable(sortBy).map(String::trim).orElse("");
//
//        if (!trimmedSortBy.isEmpty()) {
//            String[] fields = trimmedSortBy.split(",");
//            if (allowedSortFields != null && !allowedSortFields.isEmpty()) {
//                validateSortFields(fields, allowedSortFields);
//            }
//            Sort sort = Sort.by(direction, Arrays.stream(fields).map(String::trim).toArray(String[]::new));
//            return sort;
//        } else {
//            // Default sort if empty
//            return Sort.by(direction, SORT_BY_CREATED_AT);
//        }
//    }
//
//    private static void validateSortFields(String[] fields, Set<String> allowed) {
//        Set<String> invalid = new HashSet<>();
//        for (String field : fields) {
//            String trimmed = field.trim();
//            if (!allowed.contains(trimmed)) {
//                invalid.add(trimmed);
//            }
//        }
//        if (!invalid.isEmpty()) {
//            throw new IllegalArgumentException("Invalid sort fields: " + invalid + ". Allowed: " + allowed);
//        }
//    }
//}


//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.util.StringUtils;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//public final class PageUtils {
//
//    public static final int DEFAULT_PAGE = 1;
//    public static final int DEFAULT_PAGE_SIZE = 10;
//    public static final int MAX_PAGE_SIZE = 100;
//    public static final int MAX_PAGE_NUMBER = 1_000_000;
//    public static final String DEFAULT_SORT_BY = "createdAt";
//    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;
//
//    // Pre-compiled common sort directions for case-insensitive comparison
//    private static final Set<String> VALID_DIRECTIONS = Set.of("asc", "desc", "ascending", "descending");
//
//    private PageUtils() {
//        throw new AssertionError("Cannot instantiate utility class");
//    }
//
//    /**
//     * Creates a PageRequest with validation and defaults.
//     * This method supports multi-field sorting using comma-separated values for sortBy and sortDirection.
//     * If multiple sortDirections are provided, they are applied in order to the sortBy fields.
//     * If only one direction is provided for multiple fields, it is applied to all fields.
//     *
//     * @param pageNumber     The page number (1-indexed). Defaults to 1 if invalid.
//     * @param pageSize       The number of items per page. Defaults to DEFAULT_PAGE_SIZE, capped at MAX_PAGE_SIZE.
//     * @param sortBy         Comma-separated list of fields to sort by. Must be in the allowedSortFields allow-list.
//     * @param sortDirection  Comma-separated list of directions (asc, desc, ascending, descending). Defaults to DESC.
//     * @param allowedSortFields A Set of field names that are safe to sort by. Prevents SQL injection and errors.
//     * @return A validated PageRequest object.
//     * @throws IllegalArgumentException if the page number exceeds the maximum or a sortBy field is not allowed.
//     */
//    public static Pageable createPageRequest(int pageNumber,
//                                             int pageSize,
//                                             String sortBy,
//                                             String sortDirection,
//                                             Set<String> allowedSortFields) {
//
//        // 1. Validate Page Number
//        if (pageNumber > MAX_PAGE_NUMBER) {
//            throw new IllegalArgumentException("Page number cannot be larger than " + MAX_PAGE_NUMBER);
//        }
//        // Convert to 0-indexed, ensure it's at least 0.
//        int resolvedPage = Math.max(pageNumber - 1, 0);
//
//        // 2. Validate and Cap Page Size
//        int resolvedPageSize = (pageSize < 1) ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
//
//        // 3. Resolve and Validate the Sort
//        Sort resolvedSort = resolveSort(sortBy, sortDirection, allowedSortFields);
//
//        // Use the resolved page, size, and sort object
//        return PageRequest.of(resolvedPage, resolvedPageSize, resolvedSort);
//    }
//
//    /**
//     * Resolves the Sort object from request parameters, applying security and defaults.
//     */
//    private static Sort resolveSort(String sortBy, String sortDirection, Set<String> allowedSortFields) {
//        // 3a. Determine which fields to sort by. Default if empty.
//        List<String> sortByFields = StringUtils.hasText(sortBy)
//                ? Arrays.stream(sortBy.split(","))
//                        .map(String::trim)
//                        .filter(f -> !f.isEmpty())
//                        .collect(Collectors.toList())
//                : List.of(DEFAULT_SORT_BY);
//
//        // 3b. SECURITY: Validate every requested field is in the allow-list.
//        for (String field : sortByFields) {
//            if (!allowedSortFields.contains(field)) {
//                throw new IllegalArgumentException("Invalid sort field: '" + field + "'. Allowed fields are: " + allowedSortFields);
//            }
//        }
//
//        // 3c. Process the direction(s)
//        List<String> sortDirections = StringUtils.hasText(sortDirection)
//                ? Arrays.stream(sortDirection.split(","))
//                        .map(String::trim)
//                        .filter(d -> !d.isEmpty())
//                        .collect(Collectors.toList())
//                : List.of();
//
//        // 3d. Build the List of Sort.Order objects
//        List<Sort.Order> orders = new java.util.ArrayList<>();
//
//        for (int i = 0; i < sortByFields.size(); i++) {
//            String field = sortByFields.get(i);
//            String directionValue;
//
//            // Use the corresponding direction, or the first one, or the default if none.
//            if (i < sortDirections.size()) {
//                directionValue = sortDirections.get(i);
//            } else if (!sortDirections.isEmpty()) {
//                directionValue = sortDirections.get(0);
//            } else {
//                directionValue = DEFAULT_SORT_DIRECTION.name();
//            }
//
//            // Validate and parse the direction string
//            Sort.Direction direction = parseDirection(directionValue);
//            orders.add(new Sort.Order(direction, field));
//        }
//
//        return Sort.by(orders);
//    }
//
//    /**
//     * Parses a string into a Sort.Direction, defaulting to DEFAULT_SORT_DIRECTION if invalid.
//     * This is a graceful fallback for user-friendliness.
//     */
//    private static Sort.Direction parseDirection(String directionValue) {
//        if (!StringUtils.hasText(directionValue)) {
//            return DEFAULT_SORT_DIRECTION;
//        }
//        // Normalize the input for easier comparison
//        String normalized = directionValue.trim().toLowerCase();
//        // Check against pre-defined set for efficiency
//        if (!VALID_DIRECTIONS.contains(normalized)) {
//            return DEFAULT_SORT_DIRECTION;
//        }
//        // The fromString method is robust enough for "asc", "desc", "ASC", "DESC"
//        try {
//            return Sort.Direction.fromString(normalized);
//        } catch (IllegalArgumentException e) {
//            return DEFAULT_SORT_DIRECTION;
//        }
//    }
//}