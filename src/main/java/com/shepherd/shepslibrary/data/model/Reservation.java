package com.shepherd.shepslibrary.data.model;


import com.shepherd.shepslibrary.data.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_createdAt", columnList = "createdAt")
})
public class Reservation extends BaseEntity {
    @OneToOne
    private Book book;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private Instant reservationDateTime;
}
