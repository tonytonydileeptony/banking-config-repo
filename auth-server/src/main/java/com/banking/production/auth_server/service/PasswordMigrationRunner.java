package com.banking.production.auth_server.service;

// Repository for database operations
import com.banking.production.auth_server.repository.UserRepository;
// User model
import com.banking.production.auth_server.model.User;
// SLF4J logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Spring CommandLineRunner for startup tasks
import org.springframework.boot.CommandLineRunner;
// Component annotation
import org.springframework.stereotype.Component;
// Dependency injection
import org.springframework.beans.factory.annotation.Autowired;
// Password encoder
import org.springframework.security.crypto.password.PasswordEncoder;

// For list operations
import java.util.List;

// Spring component to run on application startup
@Component
// Runs on application startup to migrate plain-text passwords to BCrypt encoded
public class PasswordMigrationRunner implements CommandLineRunner {
    // Logger for password migration status and errors
    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    // Repository for database operations
    @Autowired
    private UserRepository userRepository;

    // Password encoder for hashing passwords
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Run method executed automatically on application startup
    @Override
    public void run(String... args) throws Exception {
        try {
            // Fetch all users from database
            List<User> users = userRepository.findAll();
            // Counter to track how many passwords were migrated
            int migrated = 0;

            // Iterate through each user
            for (User u : users) {
                // Get the password for current user
                String pw = u.getPassword();
                // Skip if password is null
                if (pw == null) continue;

                // Check if password is already BCrypt encoded
                // Common BCrypt prefixes: $2a$, $2b$, $2y$ (different rounds), or Spring format {bcrypt}
                if (!(pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$") || pw.startsWith("{bcrypt}"))) {
                    // Password is not encoded - encode it with BCrypt
                    String encoded = passwordEncoder.encode(pw);
                    // Update user password with encoded value
                    u.setPassword(encoded);
                    // Save the updated user to database
                    userRepository.save(u);
                    // Increment migration counter
                    migrated++;
                    // Log successful migration
                    logger.info("Migrated password for user {} to BCrypt.", u.getName());
                }
            }

            // Log migration summary
            if (migrated > 0) {
                logger.info("Password migration: {} users updated to BCrypt.", migrated);
            } else {
                logger.info("Password migration: no users required migration.");
            }
        } catch (Exception ex) {
            // Catch exceptions to prevent application startup failure
            // Log the error but continue application startup
            logger.warn("Password migration runner encountered an error: {}", ex.getMessage());
        }
    }
}
