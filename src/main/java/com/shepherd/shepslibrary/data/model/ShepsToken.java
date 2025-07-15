package com.shepherd.shepslibrary.data.model;


import com.shepherd.shepslibrary.data.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_token", columnList = "token"),
        @Index(name = "idx_createdAt", columnList = "createdAt")
})
public class ShepsToken extends BaseEntity {
    @Column(unique = true, length = 500)
    private String token;
    @Column(length = 500)
    private String refreshToken;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    private boolean isExpired;
    private boolean isRevoked;
    private LocalDateTime expirationTime;
}
