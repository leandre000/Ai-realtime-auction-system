package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.DTOs.AuthRequest;
import com.webapp.realtimeauctionbackend.DTOs.AuthResponse;
import com.webapp.realtimeauctionbackend.DTOs.RegisterRequest;
import com.webapp.realtimeauctionbackend.models.Person;
import com.webapp.realtimeauctionbackend.repositories.PersonRepository;
import com.webapp.realtimeauctionbackend.utils.JwtUtils;
import com.webapp.realtimeauctionbackend.constants.Role;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// controller/AuthController.java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public AuthController(@Lazy AuthenticationManager authManager, JwtUtils jwtUtils, PersonRepository personRepo, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.personRepo = personRepo;
        this.encoder = encoder;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Attempting to register user with email: {}", request.email());
            
            if (personRepo.existsByEmail(request.email())) {
                logger.warn("Registration failed: Email already exists - {}", request.email());
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            Person user = new Person();
            user.setName(request.name());
            user.setEmail(request.email());
            user.setPassword(encoder.encode(request.password()));
            user.setRole(Role.USER);

            personRepo.save(user);
            logger.info("User registered successfully: {}", request.email());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("email", user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            logger.info("Attempting to login user: {}", request.email());
            
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
                )
            );

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtils.generateToken(user);
            
            logger.info("User logged in successfully: {}", request.email());
            
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }
}