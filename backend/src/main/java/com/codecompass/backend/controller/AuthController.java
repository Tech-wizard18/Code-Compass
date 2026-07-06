package com.codecompass.backend.controller;

import com.codecompass.backend.dto.AuthResponse;
import com.codecompass.backend.dto.LoginRequest;
import com.codecompass.backend.dto.RegisterRequest;
import com.codecompass.backend.entity.Role;
import com.codecompass.backend.entity.UserEntity;
import com.codecompass.backend.repository.UserRepository;
import com.codecompass.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        UserEntity newUser = new UserEntity( request.getEmail(), request.getUsername(), hashedPassword, Role.USER);
        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser.getUsername(), newUser.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, newUser.getUsername(), newUser.getRole().name()));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UserEntity user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole().name()));
    }
}