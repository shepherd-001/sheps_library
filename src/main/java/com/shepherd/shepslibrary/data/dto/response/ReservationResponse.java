package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
public class ReservationResponse {
    private UUID reservationId;
    private UUID userId;
    private UUID bookId;
    private LocalDate reservationDate;
}
