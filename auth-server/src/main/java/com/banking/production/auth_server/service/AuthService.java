package com.banking.production.auth_server.service;

// User registration request DTO
import com.banking.production.auth_server.dto.UserRequestDto;
// User entity model
import com.banking.production.auth_server.model.User;
// Repository for database operations
import com.banking.production.auth_server.repository.UserRepository;
// Dependency injection annotation
import org.springframework.beans.factory.annotation.Autowired;
// Spring Security authentication manager
import org.springframework.security.authentication.AuthenticationManager;
// Authentication token for credentials
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// BCrypt password encoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// Password encoder interface
import org.springframework.security.crypto.password.PasswordEncoder;
// Service annotation
import org.springframework.stereotype.Service;

// Spring service annotation to register this as a service bean
@Service
// Service class for user authentication (registration and login)
public class AuthService {

    // Repository for database operations on users
    private final UserRepository userRepo;
    // Interface for encoding passwords
    private final PasswordEncoder encoder;
    // BCrypt encoder for password hashing
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    // JWT utility for token generation
    private final JwtUtil jwtUtil;
    // Spring Security manager for authentication
    private final AuthenticationManager authenticationManager;

    // Constructor with all dependencies injected
    @Autowired
    public AuthService(UserRepository userRepo, PasswordEncoder encoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    // Method to register a new user
    public String register(UserRequestDto req) {

        // Check if user with the same name already exists
        if (userRepo.findByName(req.getName()).isPresent()) {
            // Throw exception to prevent duplicate user registration
            throw new RuntimeException("User already exists");
        }

        // Create a new User entity object
        User user = new User();
        // Set the user's name from request
        user.setName(req.getName());
        // Set the user's password after encoding it with BCrypt (for security)
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        // Set the user's email address
        user.setEmail(req.getEmail());

        // Save the new user to the database
        userRepo.save(user);

        // Return success message
        return "User registered successfully";
    }

    // Method to authenticate user and generate JWT token
    public String login(UserRequestDto request) {
        // Log the email attempting to login
        System.out.println("Attempting login for: " + request.getEmail());

        // Authenticate the user using email and password
        // This will throw exception if credentials are invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Generate and return JWT token for authenticated user
        return jwtUtil.generateToken(request.getEmail());
    }

}
