package com.shepherd.shepslibrary.data.model;


import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Reservation extends BaseModel{
    private UUID userId;
    private UUID bookId;
    private LocalDate reservationDate;
}
