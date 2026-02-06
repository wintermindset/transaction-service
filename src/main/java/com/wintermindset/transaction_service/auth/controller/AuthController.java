package com.wintermindset.transaction_service.auth.controller;

import com.wintermindset.transaction_service.auth.dto.request.AuthRequest;
import com.wintermindset.transaction_service.auth.dto.request.RegisterRequest;
import com.wintermindset.transaction_service.auth.dto.response.AuthResponse;
import com.wintermindset.transaction_service.user.entity.UserEntity;
import com.wintermindset.transaction_service.user.enums.UserRole;
import com.wintermindset.transaction_service.user.exception.UserNotFoundException;
import com.wintermindset.transaction_service.user.service.UserService;
import com.wintermindset.transaction_service.security.jwt.JwtTokenProvider;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            UserEntity user = userService.findByUsername(request.username())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            UserDetails userDetails = User.builder()
                    .username(user.getUsername())
                    .password(user.getPasswordHash())
                    .roles(user.getUserRole().toString())
                    .build();
            String token = jwtTokenProvider.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userService.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        userService.createUser(request.username(), request.password(), UserRole.USER, Instant.now());
        return ResponseEntity.ok("User registered successfully");
    }
}
