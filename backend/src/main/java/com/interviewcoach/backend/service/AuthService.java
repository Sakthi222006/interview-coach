// src/main/java/com/interviewcoach/service/AuthService.java

package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.AuthResponse;
import com.interviewcoach.backend.dto.LoginRequest;
import com.interviewcoach.backend.dto.SignupRequest;
import com.interviewcoach.backend.model.User;
import com.interviewcoach.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // BCrypt encoder
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ─────────────────────────────────────────────────────────
    // SIGNUP LOGIC
    // ─────────────────────────────────────────────────────────
    public AuthResponse signup(SignupRequest request) {

        // 1. Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered. Please login.");
        }

        // 2. Build the new User object
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                // NEVER store plain text password!
                // BCrypt converts "password123" → "$2a$10$xyz..." (irreversible hash)
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        // 3. Save user to database
        // Hibernate runs: INSERT INTO users (name, email, password, role) VALUES (...)
        User savedUser = userRepository.save(user);

        // 4. Generate JWT token for this user
        String jwtToken = jwtService.generateToken(savedUser);

        // 5. Return the token + user info to React
        return AuthResponse.builder()
                .token(jwtToken)
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .userId(savedUser.getId())
                .message("Account created successfully!")
                .build();
    }

    // ─────────────────────────────────────────────────────────
    // LOGIN LOGIC
    // ─────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {

        // 1. Authenticate — Spring Security checks email + password
        // This internally calls:
        //   a. loadUserByUsername(email) → gets user from DB
        //   b. passwordEncoder.matches(inputPassword, storedHashedPassword)
        // If credentials are wrong, it throws an exception automatically
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. If we reach here, credentials are correct!
        // Load the user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Generate a fresh JWT token
        String jwtToken = jwtService.generateToken(user);

        // 4. Return token + user info to React
        return AuthResponse.builder()
                .token(jwtToken)
                .name(user.getName())
                .email(user.getEmail())
                .userId(user.getId())
                .message("Login successful!")
                .build();
    }
}