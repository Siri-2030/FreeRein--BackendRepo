	package com.hr.freerein.controller;
	
	import com.hr.freerein.dto.ApiResponse;
	import com.hr.freerein.dto.LoginRequest;
	import com.hr.freerein.dto.LoginResponse;
	import com.hr.freerein.dto.RegisterRequest;
	import com.hr.freerein.service.AuthService;
	import com.hr.freerein.service.UserService;
	import org.springframework.security.core.Authentication; 
	import com.hr.freerein.entity.User; 
	import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

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
	    @Autowired
	    private UserService userService;  // 👈 ADD THIS
	    
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
	    
		/*
		 * @GetMapping("/me")
		 * 
		 * @PreAuthorize("isAuthenticated()") public ResponseEntity<ApiResponse<Object>>
		 * getCurrentUser() { return ResponseEntity.ok(ApiResponse.success(null,
		 * "User info")); }
		 */
	    
		/*
		 * @GetMapping("/me") public ResponseEntity<ApiResponse<User>>
		 * getCurrentUser(Authentication auth) { UserDetails userDetails = (UserDetails)
		 * auth.getPrincipal(); User user =
		 * userService.findByEmail(userDetails.getUsername());
		 * 
		 * // 👇 LOGIC - make sure this returns REAL user data
		 * System.out.println("ME ENDPOINT USER: " + user);
		 * 
		 * return ResponseEntity.ok(ApiResponse.success(user)); // ← NOT NULL! }
		 */
	    
	    @GetMapping("/me")
	    @PreAuthorize("isAuthenticated()")
	    public ResponseEntity<ApiResponse<User>> getCurrentUser(Authentication authentication) {
	        try {
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());  // ✅ Optional
	            
	            if (optionalUser.isEmpty()) {  // ✅ Check Optional
	                System.err.println("❌ User not found: " + userDetails.getUsername());
	                return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(ApiResponse.error("User not found", HttpStatus.NOT_FOUND));
	            }
	            
	            User user = optionalUser.get();  // ✅ Extract User
	            
	            System.out.println("🔥 ME ENDPOINT: " + user.getEmail() + " | Role: " + user.getRole());
	            
	            return ResponseEntity.ok(ApiResponse.success(user, "User info"));
	        } catch (Exception e) {
	            System.err.println("❌ /auth/me ERROR: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(ApiResponse.error("Failed to fetch user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
	        }
	    }
	    
	}