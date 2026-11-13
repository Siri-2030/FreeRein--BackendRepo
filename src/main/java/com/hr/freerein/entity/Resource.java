package com.hr.freerein.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "resources")
public class Resource extends BaseEntity {
    
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @Size(max = 500)
    @Column(length = 500)
    private String description;
    
    @NotBlank
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    private String contactInfo;

    // Fields used by the React app (wrappers: old rows have NULL = default)
    @Column(length = 30)
    private String targetAudience;

    private Boolean published;

    private Integer priority;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String tagsCsv;
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
    
    public String getTargetAudience() { return targetAudience == null ? "all" : targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public boolean isPublished() { return published == null || published; }
    public void setPublished(Boolean published) { this.published = published; }

    public int getPriority() { return priority == null ? 0 : priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    @JsonProperty("tags")
    public List<String> getTags() {
        if (tagsCsv == null || tagsCsv.isBlank()) return List.of();
        return Arrays.stream(tagsCsv.split(",")).map(String::trim).filter(t -> !t.isEmpty()).toList();
    }

    @JsonProperty("tags")
    public void setTags(List<String> tags) {
        this.tagsCsv = (tags == null || tags.isEmpty()) ? null : String.join(",", tags);
    }
}