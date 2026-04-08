package com.hr.freerein.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "case_notes")
public class CaseNote extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String note;
    
    // ✅ MANUAL GETTERS/SETTERS
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}