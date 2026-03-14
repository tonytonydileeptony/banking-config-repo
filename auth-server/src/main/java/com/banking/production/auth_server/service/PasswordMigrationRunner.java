package com.banking.production.auth_server.service;


import com.banking.production.auth_server.repository.UserRepository;
import com.banking.production.auth_server.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            List<User> users = userRepository.findAll();
            int migrated = 0;
            for (User u : users) {
                String pw = u.getPassword();
                if (pw == null) continue;
                // Common BCrypt prefixes: $2a$, $2b$, $2y$
                if (!(pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$") || pw.startsWith("{bcrypt}"))) {
                    String encoded = passwordEncoder.encode(pw);
                    u.setPassword(encoded);
                    userRepository.save(u);
                    migrated++;
                    logger.info("Migrated password for user {} to BCrypt.", u.getName());
                }
            }
            if (migrated > 0) {
                logger.info("Password migration: {} users updated to BCrypt.", migrated);
            } else {
                logger.info("Password migration: no users required migration.");
            }
        } catch (Exception ex) {
            // Don't fail application startup just because migration had an issue.
            logger.warn("Password migration runner encountered an error: {}", ex.getMessage());
        }
    }
}
