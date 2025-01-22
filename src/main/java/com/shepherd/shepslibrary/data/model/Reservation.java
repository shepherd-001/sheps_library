package com.shepherd.shepslibrary.data.model;


import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Reservation extends BaseModel{
    @OneToOne
    private Book book;
    @OneToOne
    private User user;
    private LocalDate reservationDate;
}
