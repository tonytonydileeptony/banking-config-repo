package com.banking.production.auth_server.model;

// JPA entity annotations
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Maps this class to 'permission' database table
@Entity
// Entity for storing user permissions (access control)
public class Permission {

    // Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Unique identifier for each permission
    private Long id;

    // Permission name describing the action allowed (examples: READ_ACCOUNT, CREATE_TXN, DELETE_USER)
    private String name;
}
