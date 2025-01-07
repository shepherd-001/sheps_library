package com.shepherd.shepslibrary.data.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShepsToken extends BaseModel{
    @Column(unique = true, columnDefinition = "Text")
    private String token;
    @Column(unique = true, columnDefinition = "Text")
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
