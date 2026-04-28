package com.hr.freerein.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hr.freerein.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;  // ✅ Fixed: lombok.Value removed

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // ✅ Fixed: JJWT 0.11.5 syntax
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

	/*
	 * public String generateToken(UserDetails userDetails) { Map<String, Object>
	 * claims = new HashMap<>(); return createToken(claims,
	 * userDetails.getUsername()); }
	 * 
	 * public String generateToken(UserDetails userDetails, Map<String, Object>
	 * extraClaims) { return createToken(extraClaims, userDetails.getUsername()); }
	 */
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 👇 ADD ROLE TO TOKEN
        if (userDetails instanceof User) {
            claims.put("role", ((User) userDetails).getRole().name());
        }
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        // 👇 ADD ROLE TO TOKEN
        if (userDetails instanceof User) {
            claims.put("role", ((User) userDetails).getRole().name());
        }
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> extraClaims, String subject) {
        // ✅ Fixed: .claims() → .setClaims()
        return Jwts.builder()
                .setClaims(extraClaims)  // ✅ CORRECT METHOD
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // ✅ Explicit algorithm
                .compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}