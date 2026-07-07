package com.shepherd.shepslibrary.data.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        Instant reservationDate
) {}