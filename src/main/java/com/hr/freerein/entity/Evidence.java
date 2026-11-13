package com.hr.freerein.entity;

import jakarta.persistence.*;

/** Evidence Vault entry (metadata: title, description, date, file name/type). Private to its owner. */
@Entity
@Table(name = "evidence")
public class Evidence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String dateOfIncident;

    private String fileName;

    @Column(length = 30)
    private String fileType;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDateOfIncident() { return dateOfIncident; }
    public void setDateOfIncident(String dateOfIncident) { this.dateOfIncident = dateOfIncident; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}
