package com.banking.production.auth_server.mapper;


import com.banking.production.auth_server.dto.UserRequestDto;
import com.banking.production.auth_server.dto.UserResponseDto;
import com.banking.production.auth_server.model.User;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(UserRequestDto dto);

    List<UserResponseDto> toDtoList(List<User> all);
}