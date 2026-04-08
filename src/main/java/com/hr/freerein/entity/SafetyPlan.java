package com.hr.freerein.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "safety_plans")
public class SafetyPlan extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String steps;
    private String emergencyContacts;
    private boolean active = true;
    private String notes;
    
    // ✅ MANUAL GETTERS/SETTERS
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    
    public String getEmergencyContacts() { return emergencyContacts; }
    public void setEmergencyContacts(String emergencyContacts) { this.emergencyContacts = emergencyContacts; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}