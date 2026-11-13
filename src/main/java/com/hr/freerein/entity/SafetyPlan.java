package com.hr.freerein.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "safety_plans")
public class SafetyPlan extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String steps;
    @Column(columnDefinition = "TEXT")
    private String emergencyContacts;
    @Column(columnDefinition = "TEXT")
    private String notes;

    // The React form stores lists as JSON text
    @Column(columnDefinition = "TEXT")
    private String safePlaces;
    @Column(columnDefinition = "TEXT")
    private String importantDocuments;
    @Column(columnDefinition = "TEXT")
    private String safetySteps;
    @Column(columnDefinition = "TEXT")
    private String supportServices;
    @Column(columnDefinition = "TEXT")
    private String personalNotes;
    
    // ✅ MANUAL GETTERS/SETTERS
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    
    public String getEmergencyContacts() { return emergencyContacts; }
    public void setEmergencyContacts(String emergencyContacts) { this.emergencyContacts = emergencyContacts; }
    
    public String getSafePlaces() { return safePlaces; }
    public void setSafePlaces(String v) { this.safePlaces = v; }
    public String getImportantDocuments() { return importantDocuments; }
    public void setImportantDocuments(String v) { this.importantDocuments = v; }
    public String getSafetySteps() { return safetySteps; }
    public void setSafetySteps(String v) { this.safetySteps = v; }
    public String getSupportServices() { return supportServices; }
    public void setSupportServices(String v) { this.supportServices = v; }
    public String getPersonalNotes() { return personalNotes; }
    public void setPersonalNotes(String v) { this.personalNotes = v; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}