package com.hr.freerein.dto;

import com.hr.freerein.entity.User;

public class LoginResponse {
    private String token;
    private User user;
    private String role;
    private String message;
    
    // ✅ MANUAL CONSTRUCTORS
    public LoginResponse() {}
    
    // ✅ FIXED: role.toString()
    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
        this.role = user.getRole().name();  // ✅ Enum → String
        this.message = "Login successful";
    }
    
    public LoginResponse(String token, User user, String role, String message) {
        this.token = token;
        this.user = user;
        this.role = role;
        this.message = message;
    }
    
    // ✅ MANUAL GETTERS/SETTERS
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}