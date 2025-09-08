package com.Nisal.Agrimatch.dto;

public class UserLoginResponse {
    private String token;
    private String name;
    private String role;

    // Constructor
    public UserLoginResponse(String token, String name, String role) {
        this.token = token;
        this.name = name;
        this.role = role;
    }

    // Getters only (no need for setters usually)
    public String getToken() { return token; }
    public String getName() { return name; }
    public String getRole() { return role; }
}
