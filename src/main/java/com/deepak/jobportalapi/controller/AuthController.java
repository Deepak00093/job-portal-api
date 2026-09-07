package com.deepak.jobportalapi.controller;

import com.deepak.jobportalapi.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.deepak.jobportalapi.entity.User;
import com.deepak.jobportalapi.service.JwtService;
import com.deepak.jobportalapi.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginRequestDTO requestDTO) {

        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        requestDTO.getEmail(),
                        requestDTO.getPassword()
                )
        );

        User user = userService.getUserByEmail(requestDTO.getEmail());

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(token);
    }
}