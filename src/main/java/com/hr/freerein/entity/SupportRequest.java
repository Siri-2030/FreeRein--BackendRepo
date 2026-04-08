package com.hr.freerein.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "support_requests")
public class SupportRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String type;
    private String description;
    private String status = "PENDING";
    private String assignedSupporter;
    
    // ✅ MANUAL GETTERS/SETTERS
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAssignedSupporter() { return assignedSupporter; }
    public void setAssignedSupporter(String assignedSupporter) { this.assignedSupporter = assignedSupporter; }
}