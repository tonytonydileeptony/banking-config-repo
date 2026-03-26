package com.banking.production.auth_server.service;

// Spring configuration annotation
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Spring Security authentication classes
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// Authentication configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// HTTP security configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// User details service
import org.springframework.security.core.userdetails.UserDetailsService;
// Password encoders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Security filter chain
import org.springframework.security.web.SecurityFilterChain;

// Spring configuration annotation to register this as a configuration class
@Configuration
// Enables Spring Security web security features
@EnableWebSecurity

// Configuration class for Spring Security setup (authentication, authorization, filters)
public class SecurityConfig {

    // Service for loading user details from database
    private final UserDetailsService userDetailsService;

    // Constructor with UserDetailsService dependency injected
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Bean method to provide password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt for password hashing (industry standard)
        return new BCryptPasswordEncoder();
    }

    // Bean method to provide authentication provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Create DAO-based authentication provider (queries database for users)
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Set the service for loading user details from database
        provider.setUserDetailsService(userDetailsService);
        // Set the password encoder for verification
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Bean method to configure the security filter chain
    @Bean
    SecurityFilterChain security(HttpSecurity http,
                                 // JWT filter for token validation
                                 JwtFilter jwtFilter,
                                 // Authentication provider for user authentication
                                 AuthenticationProvider authenticationProvider)
            throws Exception {

        http
                // Disable CSRF protection (usually disabled for stateless APIs)
                .csrf(csrf -> csrf.disable())
                // Set the authentication provider (required for authentication)
                .authenticationProvider(authenticationProvider)
                // Configure request authorization
                .authorizeHttpRequests(auth -> auth
                        // Allow all requests to /auth/** endpoints (login, register) without authentication
                        .requestMatchers("/auth/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Add JWT filter before the standard username/password authentication filter
                // This ensures tokens are validated before basic auth attempts
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        // Build and return the security filter chain
        return http.build();
    }

    // Bean method to provide authentication manager
    @Bean
    AuthenticationManager authenticationManager(
            // Fetch the authentication manager from Spring Security configuration
            AuthenticationConfiguration config) throws Exception {
        // Return the authentication manager for authenticating users
        return config.getAuthenticationManager();
    }
}
