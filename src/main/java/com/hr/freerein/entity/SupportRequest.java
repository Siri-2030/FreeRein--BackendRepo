package com.hr.freerein.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "support_requests")
public class SupportRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String type;                 // request type: counselling, legal_advice, emergency, general

    @Column(length = 200)
    private String subject;

    @Column(length = 20)
    private String urgency;

    private Boolean anonymous;

    @Column(columnDefinition = "TEXT")
    private String notes;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String status = "PENDING";
    private String assignedSupporter;
    
    // ✅ MANUAL GETTERS/SETTERS
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getUrgency() { return urgency == null ? "MEDIUM" : urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public boolean isAnonymous() { return anonymous != null && anonymous; }
    public void setAnonymous(Boolean anonymous) { this.anonymous = anonymous; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAssignedSupporter() { return assignedSupporter; }
    public void setAssignedSupporter(String assignedSupporter) { this.assignedSupporter = assignedSupporter; }
}