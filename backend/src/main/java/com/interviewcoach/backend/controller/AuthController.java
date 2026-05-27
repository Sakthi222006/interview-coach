// src/main/java/com/interviewcoach/controller/AuthController.java

package com.interviewcoach.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewcoach.backend.dto.AuthResponse;
import com.interviewcoach.backend.dto.LoginRequest;
import com.interviewcoach.backend.dto.SignupRequest;
import com.interviewcoach.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController // This class handles HTTP requests and returns JSON
@RequestMapping("/api/auth") // All routes in this class start with /api/auth
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── POST /api/auth/signup ──
    // React sends: { name, email, password }
    // We return: { token, name, email, userId, message }
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest request
            // @Valid = run the validation rules from SignupRequest
            // @RequestBody = parse the JSON body into a SignupRequest object
    ) {
        try {
            AuthResponse response = authService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            // 201 Created = success, new resource was created
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.builder().message(e.getMessage()).build());
            // 409 Conflict = email already exists
        }
    }

    // ── POST /api/auth/login ──
    // React sends: { email, password }
    // We return: { token, name, email, userId, message }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
            // 200 OK = success
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder().message("Invalid email or password").build());
            // 401 Unauthorized = wrong credentials
        }
    }

    // ── GET /api/auth/health ──
    // Simple check to confirm the backend is running
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Interview Coach Backend is running!"
        ));
    }
}