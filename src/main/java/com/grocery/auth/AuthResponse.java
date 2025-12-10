package com.grocery.auth;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String role;
    private String email;
    private String userName;

    public AuthResponse(String token, String role, String email, String userName) {
        this.token = token;
        this.role = role;
        this.email = email;
        this.userName = userName;
    }
}
