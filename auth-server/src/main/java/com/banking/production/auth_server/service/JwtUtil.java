package com.banking.production.auth_server.service;

// JWT token generation and parsing library
import io.jsonwebtoken.*;
// For HMAC key generation
import io.jsonwebtoken.security.Keys;
// Spring component annotation
import org.springframework.stereotype.Component;

// For cryptographic key handling
import java.security.Key;
// For token expiration timestamps
import java.util.Date;

// Spring component annotation to register this as a bean
@Component
// Utility class for JWT token operations (generation, extraction, validation)
public class JwtUtil {

    // Secret key for signing JWT tokens (should be externalized to properties)
    private final String SECRET = "mysecretkeymysecretkeymysecretkey12345";

    // Method to generate HMAC SHA key from the secret
    private Key getKey() {
        // Convert secret string to bytes and create HMAC SHA key
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Method to generate a JWT token for a user (email)
    public String generateToken(String email) {
        // Use JWT builder to create token
        return Jwts.builder()
                // Set the subject (user identifier - email)
                .setSubject(email)
                // Set the token issued-at time
                .setIssuedAt(new Date())
                // Set token expiration: 10 hours from current time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                // Sign the token with HMAC SHA-256 algorithm
                .signWith(getKey(), SignatureAlgorithm.HS256)
                // Compact and serialize the token to a string
                .compact();
    }

    // Method to extract the email (subject) from a JWT token
    public String extractEmail(String token) {
        // Log token extraction for debugging
        System.out.println("Extracting email from token: " + token);
        // Parse the token using the secret key
        return Jwts.parserBuilder()
                // Set the signing key for verification
                .setSigningKey(getKey())
                // Build the parser
                .build()
                // Parse and verify the signed JWT
                .parseClaimsJws(token)
                // Get the token payload (claims)
                .getBody()
                // Extract and return the subject (email)
                .getSubject();
    }

    // Method to validate if a JWT token is valid
    public boolean isValid(String token) {
        // Log token validation for debugging
        System.out.println("Validating token: " + token);
        try {
            // Attempt to parse and verify the token
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            // Return true if token is valid (not expired, properly signed)
            return true;
        } catch (Exception e) {
            // Return false if any exception occurs during validation (invalid, expired, tampered)
            return false;
        }
    }
}
