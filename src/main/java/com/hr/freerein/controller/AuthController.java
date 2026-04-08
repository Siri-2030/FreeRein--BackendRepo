package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.LoginRequest;
import com.hr.freerein.dto.LoginResponse;
import com.hr.freerein.dto.RegisterRequest;
import com.hr.freerein.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;  // ✅ Added
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {
    
    @Autowired  // ✅ Fixed: Replaces RequiredArgsConstructor
    private AuthService authService;  // ✅ Removed 'final'
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse loginResponse = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(loginResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.unauthorized("Invalid credentials"));
        }
    }
    
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }
    
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        return ResponseEntity.ok(ApiResponse.success(null, "User info"));
    }
}