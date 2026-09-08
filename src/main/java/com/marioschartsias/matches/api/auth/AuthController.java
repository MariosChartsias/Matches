package com.marioschartsias.matches.api.auth;

import com.marioschartsias.matches.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final String username;
    private final String password;
    private final long expirationMinutes;

    public AuthController(
            JwtService jwtService,
            @Value("${app.auth.username}") String username,
            @Value("${app.auth.password}") String password,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.jwtService = jwtService;
        this.username = username;
        this.password = password;
        this.expirationMinutes = expirationMinutes;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        if (!credentialsMatch(request)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return ResponseEntity.ok(new TokenResponse(
                jwtService.createToken(request.username()),
                "Bearer",
                expirationMinutes * 60
        ));
    }

    private boolean credentialsMatch(LoginRequest request) {
        return MessageDigest.isEqual(
                username.getBytes(StandardCharsets.UTF_8),
                request.username().getBytes(StandardCharsets.UTF_8)
        ) && MessageDigest.isEqual(
                password.getBytes(StandardCharsets.UTF_8),
                request.password().getBytes(StandardCharsets.UTF_8)
        );
    }
}
