package com.banking.production.auth_server.service;

// User request DTO for API input
import com.banking.production.auth_server.dto.UserRequestDto;
// User response DTO for API output
import com.banking.production.auth_server.dto.UserResponseDto;
// Mapper for converting between DTOs and entities
import com.banking.production.auth_server.mapper.UserMapper;
// User entity model
import com.banking.production.auth_server.model.User;
// Repository for database operations
import com.banking.production.auth_server.repository.UserRepository;

// Dependency injection annotation
import org.springframework.beans.factory.annotation.Autowired;
// Service annotation
import org.springframework.stereotype.Service;

// For timestamp operations
import java.time.LocalDateTime;
// For list operations
import java.util.List;

// Spring service annotation to register this as a service bean
@Service

// Service class for user management operations (create, retrieve, etc.)
public class UserService {

    // Repository for database operations on users
    private final UserRepository repository;
    // Mapper for converting between User entities and DTOs
    @Autowired
    private final UserMapper mapper;

    // Constructor with dependencies injected
    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // Method to create a new user from request DTO
    public UserResponseDto createUser(UserRequestDto requestDto) {

        // Convert request DTO to User entity object
        User user = mapper.toEntity(requestDto);
        // Set the creation timestamp to current time
        user.setCreatedAt(LocalDateTime.now().toString());

        // Save the user entity to the database
        User savedUser = repository.save(user);

        // Convert the saved entity back to response DTO and return
        return mapper.toDto(savedUser);
    }

    // Method to retrieve all users from the database
    public List<UserResponseDto> getAllUsers() {
        // Fetch all users from database and convert to response DTOs
        return mapper.toDtoList(repository.findAll());
    }
}
