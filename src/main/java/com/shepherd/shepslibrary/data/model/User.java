package com.shepherd.shepslibrary.data.model;

import com.shepherd.shepslibrary.data.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_createdAt", columnList = "createdAt")
})
public class User extends BaseEntity {
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "enabled")
    private boolean isEnabled;
    @Column(name = "revoked")
    private boolean isRevoked;
}
