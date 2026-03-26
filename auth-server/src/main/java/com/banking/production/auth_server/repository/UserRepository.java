package com.banking.production.auth_server.repository;

// User entity model
import com.banking.production.auth_server.model.User;
// Spring Data JPA repository base interface
import org.springframework.data.jpa.repository.JpaRepository;
// Spring repository annotation
import org.springframework.stereotype.Repository;

// For optional return values
import java.util.Optional;

// Spring repository annotation to register this as a repository bean
@Repository
// Repository interface for User entity with primary key type Long
// Extends JpaRepository to inherit CRUD operations (Create, Read, Update, Delete)
public interface UserRepository extends JpaRepository<User, Long> {

    // Query method to find user by name
    // Spring Data JPA generates SQL: SELECT * FROM user WHERE name = ?
    // Returns Optional to handle case when user is not found
    Optional<User> findByName(String name);

    // Query method to find user by email for authentication
    // Spring Data JPA generates SQL: SELECT * FROM user WHERE email = ?
    // Add lookup by email for authentication
    Optional<User> findByEmail(String email);

}
