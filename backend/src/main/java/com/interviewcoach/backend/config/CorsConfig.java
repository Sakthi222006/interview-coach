// src/main/java/com/interviewcoach/config/CorsConfig.java

package com.interviewcoach.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

// CORS = Cross-Origin Resource Sharing
// By default, browsers BLOCK requests from one domain to another
// Our React app runs on localhost:5173
// Our Spring Boot runs on localhost:8080
// Without CORS config, React CANNOT talk to Spring Boot!
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow React frontend to call our backend
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",  // Vite dev server
            "http://localhost:3000"   // In case you use port 3000
        ));

        // Allow these HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow these headers (Authorization is important for JWT!)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // Allow cookies/auth headers to be sent
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // apply to ALL routes

        return new CorsFilter(source);
    }
}