package com.hr.freerein.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "resources")
public class Resource extends BaseEntity {
    
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @Size(max = 500)
    private String description;
    
    @NotBlank
    private String category;
    
    private String content;
    private String contactInfo;
    private String location;
    
    // ✅ REMOVED: duplicate 'Active' field - inherits from BaseEntity
    
    // ✅ MANUAL GETTERS/SETTERS (BaseEntity provides isActive()/setActive())
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    // ✅ NO isActive()/setActive() NEEDED - inherited from BaseEntity
}