package com.shepherd.shepslibrary.data.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_createdAt", columnList = "createdAt")
})
public class Reservation extends BaseModel{
    @OneToOne
    private Book book;
    @OneToOne
    private User user;
    private LocalDate reservationDate;
}
