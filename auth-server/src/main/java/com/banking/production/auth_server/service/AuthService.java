package com.banking.production.auth_server.service;


import com.banking.production.auth_server.dto.UserRequestDto;
import com.banking.production.auth_server.model.User;
import com.banking.production.auth_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public AuthService(UserRepository userRepo, PasswordEncoder encoder,JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }
//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWxlZXBAZ21haWwuY29tIiwiaWF0IjoxNzcwOTg3MDUwLCJleHAiOjE3NzEwMjMwNTB9.6zrJRcd7Bt763qFiKBlfAUnAPZqTlTiU94gVzj7Psiw
    public String register(UserRequestDto req) {

        if (userRepo.findByName(req.getName()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setName(req.getName());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());// 🔐 ENCRYPT

        userRepo.save(user);

        return "User registered successfully";
    }
    public String login(UserRequestDto request) {
System.out.println("Attempting login for: " + request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return jwtUtil.generateToken(request.getEmail());
    }

}
