package com.stockflow.service;

import com.stockflow.model.User;
import com.stockflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    /**
     * Registers a new user. Throws IllegalArgumentException on validation failure.
     */
    public User register(String username, String email, String rawPassword, String role) {
        // Check duplicates
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
            .username(username.trim())
            .email(email.trim().toLowerCase())
            .password(passwordEncoder.encode(rawPassword))  // BCrypt hashed
            .role(role)
            .build();

        return userRepo.save(user);
    }
}