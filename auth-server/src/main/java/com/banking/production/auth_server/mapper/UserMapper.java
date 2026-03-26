package com.banking.production.auth_server.mapper;

// User request DTO
import com.banking.production.auth_server.dto.UserRequestDto;
// User response DTO
import com.banking.production.auth_server.dto.UserResponseDto;
// User entity model
import com.banking.production.auth_server.model.User;
// MapStruct annotation for automatic mapper code generation
import org.mapstruct.Mapper;
// For list operations
import java.util.List;

// MapStruct mapper with Spring component model for dependency injection
@Mapper(componentModel = "spring")
// Interface for converting between User entity and DTOs using MapStruct
public interface UserMapper {

    // Converts User entity to UserResponseDto
    // MapStruct generates implementation automatically based on field name matching
    UserResponseDto toDto(User user);

    // Converts UserRequestDto to User entity
    // Maps request data to entity for database persistence
    User toEntity(UserRequestDto dto);

    // Converts list of User entities to list of UserResponseDtos
    // Used for batch conversions when retrieving multiple users
    List<UserResponseDto> toDtoList(List<User> all);
}