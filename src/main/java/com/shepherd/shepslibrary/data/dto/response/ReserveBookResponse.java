package com.shepherd.shepslibrary.data.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ReserveBookResponse {
    private String message;
    private UUID reservationId;
    private UUID bookId;
    private boolean isReserved;
}
