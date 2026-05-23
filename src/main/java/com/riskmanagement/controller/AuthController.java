package com.riskmanagement.controller;

import com.riskmanagement.dto.request.AuthRequest;
import com.riskmanagement.dto.response.ApiResponse;
import com.riskmanagement.dto.response.AuthResponse;
import com.riskmanagement.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "JWT authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.expiration-ms}")
    private Long jwtExpirationMs;

    @PostMapping("/login")
    @Operation(summary = "Authenticate and get JWT token",
            description = "Login with username/password to receive a JWT bearer token for API access")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        log.info("Authentication attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("UNKNOWN");

        AuthResponse response = AuthResponse.of(
                token, request.getUsername(), role, jwtExpirationMs);

        log.info("User {} authenticated successfully with role {}", request.getUsername(), role);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }
}
