package com.hr.freerein.config;

import com.hr.freerein.security.JwtAuthenticationFilter;
import com.hr.freerein.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, 
                         UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CORS - Global configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // ✅ CSRF Disabled (Stateless JWT API)
            .csrf(csrf -> csrf.disable())
            
            // ✅ Stateless Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // ✅ Authorization Rules - ORDER MATTERS!
            .authorizeHttpRequests(auth -> auth
                // 🎯 PUBLIC ENDPOINTS (No Authentication Required)
                .requestMatchers("/api/auth/**").permitAll()                    // AuthController
                .requestMatchers("/api/apps/public/**").permitAll()             // AppController
                .requestMatchers("/api/resources/**").permitAll()               // ResourceController (Public GETs)
                .requestMatchers("/entities/users").permitAll()                 // EntityController GET/POST
                .requestMatchers("/entities/users/**").permitAll()              // EntityController all operations
                
                // 🎯 SWAGGER/UI Documentation
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                
                // 🔐 PROTECTED ENDPOINTS (JWT Required)
                .requestMatchers("/api/cases/**").authenticated()
                .requestMatchers("/api/safety-plans/**").authenticated()
                .requestMatchers("/api/support/**").authenticated()
                
                // 👑 ADMIN ONLY
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // 🚫 Everything else requires authentication
                .anyRequest().authenticated()
            )
            
            // ✅ Authentication Provider
            .authenticationProvider(authenticationProvider())
            
            // ✅ JWT Filter BEFORE UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // ✅ Development: Allow all origins
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}