// src/main/java/com/interviewcoach/service/UserDetailsServiceImpl.java

package com.interviewcoach.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.interviewcoach.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor // Lombok: auto-generates constructor for final fields
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security calls this method when it needs to find a user
    // "username" in our app = email address
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found with email: " + email)
                );
        // Our User class implements UserDetails, so we can return it directly
    }
}