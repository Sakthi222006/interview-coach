// src/main/java/com/interviewcoach/service/JwtService.java

package com.interviewcoach.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // Tells Spring: "this is a service class, manage it for me"
public class JwtService {

    // Reads jwt.secret from application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Reads jwt.expiration from application.properties
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ─────────────────────────────────────────────────
    // STEP 1: Generate a JWT Token for a user
    // Called after successful login/signup
    // ─────────────────────────────────────────────────
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)                        // extra data to embed (optional)
                .subject(userDetails.getUsername())          // stores the user's email
                .issuedAt(new Date(System.currentTimeMillis()))  // token creation time
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // expiry
                .signWith(getSigningKey())                   // sign with our secret key
                .compact();                                  // build the token string
    }

    // ─────────────────────────────────────────────────
    // STEP 2: Validate a token
    // Returns true if token is valid AND belongs to this user
    // ─────────────────────────────────────────────────
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // get email from token
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // ─────────────────────────────────────────────────
    // Helper methods
    // ─────────────────────────────────────────────────

    // Extract the email (username) from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check if the token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract any claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Decode and parse all the data inside the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // verify using our secret key
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Convert the secret string into a secure cryptographic key
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}