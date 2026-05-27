// src/main/java/com/interviewcoach/filter/JwtAuthFilter.java

package com.interviewcoach.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.interviewcoach.backend.service.JwtService;
import com.interviewcoach.backend.service.UserDetailsServiceImpl;

import java.io.IOException;

@Component // Tells Spring to manage this as a component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter = runs ONCE per HTTP request (not multiple times)

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,   // the incoming HTTP request
            @NonNull HttpServletResponse response, // the outgoing HTTP response
            @NonNull FilterChain filterChain       // the rest of the filter chain
    ) throws ServletException, IOException {

        // ── Step 1: Get the Authorization header ──
        // React sends: Authorization: Bearer eyJhbGciOi...
        final String authHeader = request.getHeader("Authorization");

        // ── Step 2: If no token, skip this filter ──
        // (public routes like /api/auth/login don't have tokens)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // continue to next filter
            return;
        }

        // ── Step 3: Extract the token (remove "Bearer " prefix) ──
        final String jwt = authHeader.substring(7); // "Bearer " = 7 characters

        // ── Step 4: Extract the email from the token ──
        final String userEmail = jwtService.extractUsername(jwt);

        // ── Step 5: If we got an email AND user isn't already authenticated ──
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // ── Step 6: Check if the token is valid ──
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // ── Step 7: Create an authentication object ──
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // no credentials needed (we already verified)
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // ── Step 8: Tell Spring Security "this user is authenticated" ──
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ── Step 9: Continue to the actual controller ──
        filterChain.doFilter(request, response);
    }
}