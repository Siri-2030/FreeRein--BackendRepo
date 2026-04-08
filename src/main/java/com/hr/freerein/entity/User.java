package com.hr.freerein.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String name;  // ✅ Fixed: fullName → name
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Role role = Role.VICTIM;  // ✅ Fixed: userType → Role enum
    
   // @Column(name = "active")
   // private boolean active = true;
    
    // ✅ Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)	
    private List<SafetyPlan> safetyPlans;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SupportRequest> supportRequests;
    
    // ✅ MANUAL GETTERS/SETTERS
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
   // public boolean isActive() { return active; }
    //public void setActive(boolean active) { this.active = active; }
    
    // ✅ UserDetails implementation
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    public String getUsername() { return email; }
    public boolean isAccountNonExpired() { return true; }
    public boolean isAccountNonLocked() { return true; }
    public boolean isCredentialsNonExpired() { return true; }
   // public boolean isEnabled() { return active; }
    
    public enum Role {
        ADMIN, VICTIM, COUNSELLOR, LEGAL_ADVISOR
    }
}