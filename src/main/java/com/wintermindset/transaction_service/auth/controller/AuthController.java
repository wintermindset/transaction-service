package com.wintermindset.transaction_service.auth.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wintermindset.transaction_service.auth.dto.request.AuthRequest;
import com.wintermindset.transaction_service.auth.dto.request.RegisterRequest;
import com.wintermindset.transaction_service.auth.dto.response.AuthResponse;
import com.wintermindset.transaction_service.security.jwt.JwtTokenProvider;
import com.wintermindset.transaction_service.user.command.CreateUserProfileCommand;
import com.wintermindset.transaction_service.user.enums.UserRole;
import com.wintermindset.transaction_service.user.exception.BadPasswordException;
import com.wintermindset.transaction_service.user.exception.UserAlreadyExistsException;
import com.wintermindset.transaction_service.user.exception.UserNotFoundException;
import com.wintermindset.transaction_service.user.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(
            JwtTokenProvider jwtTokenProvider,
            UserService userService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            UUID userId = userService.getReferenceByUsername(request.username()).getUserId();
            boolean valid = userService.checkPassword(userId, request.password());
            if (!valid) {
                throw new BadPasswordException("Invalid password");
            }
            String token = jwtTokenProvider.generateToken(userId);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (UserNotFoundException | BadPasswordException e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            userService.createUserProfile(
                new CreateUserProfileCommand(
                    request.username(),
                    request.password(),
                    request.fullName(),
                    request.birthday(),
                    UserRole.USER
                )
            );
            return ResponseEntity.ok("User registered successfully");
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
    }
}