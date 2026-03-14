package com.banking.production.auth_server.service;



import com.banking.production.auth_server.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import com.banking.production.auth_server.model.User;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Login email: " + email);

        // Use email lookup for authentication (login sends email)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println("Login email: " + email);
        System.out.println("DB password: " + user.getPassword());
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
