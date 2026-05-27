// src/main/java/com/interviewcoach/config/SecurityConfig.java

package com.interviewcoach.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.interviewcoach.backend.filter.JwtAuthFilter;
import com.interviewcoach.backend.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration     // This class contains Spring configuration
@EnableWebSecurity // Enable Spring Security
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // ── RULE 1: Define which routes are public and which are protected ──
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
    .cors(cors -> {})
    .csrf(AbstractHttpConfigurer::disable)

            // Define URL access rules
          .authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    // All other API routes require JWT authentication
    .requestMatchers("/api/questions/**").authenticated()
    .requestMatchers("/api/sessions/**").authenticated()
    .requestMatchers("/api/ai/**").authenticated()
    .anyRequest().authenticated()
)
            // Use STATELESS sessions — no server-side sessions, only JWT
            // This means the server doesn't remember you between requests
            // The JWT token IS your "session"
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Use our custom authentication provider
            .authenticationProvider(authenticationProvider())

            // Add our JWT filter BEFORE Spring's default login filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── RULE 2: How to check username + password ──
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // how to load user
        authProvider.setPasswordEncoder(passwordEncoder());      // how to check password
        return authProvider;
    }

    // ── RULE 3: BCrypt password encoder ──
    // BCrypt is a one-way hashing algorithm
    // "password123" → "$2a$10$N9qo8uLOickgx2ZMRZo..." (never reversible)
    // When checking login: BCrypt hashes the input and compares, never decrypts
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(
            List.of("http://localhost:5173"));

    configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    configuration.setAllowedHeaders(
            List.of("*"));

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
}

    // ── RULE 4: AuthenticationManager — used in login to verify credentials ──
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}