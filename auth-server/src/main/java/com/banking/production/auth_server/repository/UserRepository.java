package com.banking.production.auth_server.repository;


import com.banking.production.auth_server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByName(String name);

        // Add lookup by email for authentication
        Optional<User> findByEmail(String email);

}
