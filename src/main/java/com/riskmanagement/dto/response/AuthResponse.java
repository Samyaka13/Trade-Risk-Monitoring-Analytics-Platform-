package com.riskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private String username;
    private String role;
    private Long expiresIn;

    public static AuthResponse of(String token, String username, String role, Long expiresIn) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(username)
                .role(role)
                .expiresIn(expiresIn)
                .build();
    }
}
