package com.riskmanagement.entity;

import com.riskmanagement.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * Application user entity for authentication and authorization.
 * <p>
 * Separate from Trader entity — not all system users are traders.
 * Risk managers and admins also need access to the platform.
 */
@Entity
@Table(name = "app_users", indexes = {
        @Index(name = "idx_user_username", columnList = "username", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}
