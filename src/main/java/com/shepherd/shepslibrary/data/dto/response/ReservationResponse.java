package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ReservationResponse {
    private String reservationId;
    private String userId;
    private String bookId;
    private LocalDate reservationDate;
}
