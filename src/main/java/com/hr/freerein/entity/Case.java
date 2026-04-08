package com.hr.freerein.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
public class Case extends BaseEntity {  // ✅ Good rename (avoids keyword conflict)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String status = "OPEN";  // OPEN, IN_PROGRESS, CLOSED, RESOLVED

    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // ✅ Optional: Case Notes relationship
    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaseNote> caseNotes = new ArrayList<>();
    
    // ✅ MANUAL GETTERS/SETTERS
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public List<CaseNote> getCaseNotes() { return caseNotes; }
    public void setCaseNotes(List<CaseNote> caseNotes) { this.caseNotes = caseNotes; }
}