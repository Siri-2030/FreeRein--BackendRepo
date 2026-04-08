package com.hr.freerein.service;

import com.hr.freerein.dto.*;
import com.hr.freerein.entity.User;
import com.hr.freerein.repository.UserRepository;
import com.hr.freerein.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    
    // ✅ MANUAL CONSTRUCTOR
    @Autowired
    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager,
                      UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }
    
    // ✅ YOUR METHODS (UNCHANGED)
    public LoginResponse register(RegisterRequest request) {
        // ✅ Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        try {
            User user = new User();
            user.setName(request.getName().trim());
            user.setEmail(request.getEmail().trim().toLowerCase());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(User.Role.valueOf(request.getUserType().toUpperCase()));
            // ✅ active is auto-set by BaseEntity
            
            User savedUser = userRepository.save(user);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
            String jwtToken = jwtService.generateToken(userDetails);
            
            return new LoginResponse(jwtToken, savedUser);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user type: " + request.getUserType());
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(userDetails);
        
        return new LoginResponse(jwtToken, user);
    }
    
    public Optional<User> getCurrentUser(String email) {
        return userRepository.findByEmail(email);
    }
}