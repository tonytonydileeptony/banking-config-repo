package com.banking.production.auth_server.model;

// JPA entity and relationship annotations
import jakarta.persistence.*;

// For token expiration timestamps
import java.time.LocalDateTime;

// Maps this class to 'refresh_tokens' database table
@Entity
@Table(name = "refresh_tokens")
// Entity for storing refresh tokens used to obtain new access tokens
public class RefreshToken {

    // Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Unique identifier for each refresh token record
    private Long id;

    // Unique refresh token string (cannot be null or duplicate)
    // The actual refresh token value issued to the client
    @Column(nullable = false, unique = true)

    private String token;

    // One-to-One relationship with AuthUser entity
    @OneToOne
    // Foreign key column referencing the user_id in auth_user table
    @JoinColumn(name = "user_id")
    // Reference to the user associated with this refresh token
    private AuthUser user;

    // Expiration date and time for the refresh token
    private LocalDateTime expiryDate;

    // Boolean flag to track if token has been revoked/invalidated
    private boolean revoked;
}
