package com.banking.production.auth_server.service;

// User repository for database operations
import com.banking.production.auth_server.repository.UserRepository;
// Spring Security interfaces for user authentication
import org.springframework.security.core.userdetails.*;
// User entity model
import com.banking.production.auth_server.model.User;
// Spring service annotation
import org.springframework.stereotype.Service;

// Spring service annotation to register this as a service bean
@Service
// Implements UserDetailsService interface for Spring Security authentication
public class CustomUserDetailsService implements UserDetailsService {

    // Repository for accessing user data from the database
    private final UserRepository userRepository;

    // Constructor with UserRepository injected
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Override method for loading user details by username (email)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Log the login email attempt for debugging
        System.out.println("Login email: " + email);

        // Query database for user by email and throw exception if not found
        // Use email lookup for authentication (login sends email)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Log the email and password from database for debugging
        System.out.println("Login email: " + email);
        System.out.println("DB password: " + user.getPassword());

        // Build and return Spring Security UserDetails object with user credentials
        return org.springframework.security.core.userdetails.User
                // Set the username to be the user's email
                .withUsername(user.getEmail())
                // Set the encoded password from database
                .password(user.getPassword())
                // Assign USER role to the user
                .roles("USER")
                // Build the UserDetails object
                .build();
    }
}
