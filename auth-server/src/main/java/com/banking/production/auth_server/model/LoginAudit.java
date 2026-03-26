package com.banking.production.auth_server.model;

// JPA entity annotations for ORM mapping
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// For timestamp storage
import java.time.LocalDateTime;

// Maps this class to 'login_audit' database table
@Entity
// Entity for tracking and auditing user login attempts
public class LoginAudit {

    // Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Unique identifier for each login audit record
    private Long id;

    // Username of the person attempting to login
    private String username;

    // Boolean flag indicating if login was successful (true) or failed (false)
    private boolean success;

    // IP address from which the login attempt originated
    private String ipAddress;

    // Timestamp when the login attempt occurred
    private LocalDateTime loginTime;
}