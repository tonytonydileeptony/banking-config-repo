package com.banking.production.auth_server.service;



import com.banking.production.auth_server.dto.UserRequestDto;
import com.banking.production.auth_server.dto.UserResponseDto;
import com.banking.production.auth_server.mapper.UserMapper;
import com.banking.production.auth_server.model.User;
import com.banking.production.auth_server.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service

public class UserService {

    private final UserRepository repository;
    @Autowired
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;

        this.mapper = mapper;
    }


    public UserResponseDto createUser(UserRequestDto requestDto) {

        User user = mapper.toEntity(requestDto);
        user.setCreatedAt(LocalDateTime.now().toString());

        User savedUser = repository.save(user);

        return mapper.toDto(savedUser);
    }

    public List<UserResponseDto> getAllUsers() {
        return mapper.toDtoList(repository.findAll());
    }
}
