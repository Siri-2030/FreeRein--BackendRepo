package com.hr.freerein.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "support_messages")
public class SupportMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_request_id")
    private SupportRequest supportRequest;

    private String senderId;
    private String content;
    private boolean fromSupporter = false;
    
    // ✅ MANUAL GETTERS/SETTERS
    public SupportRequest getSupportRequest() { return supportRequest; }
    public void setSupportRequest(SupportRequest supportRequest) { this.supportRequest = supportRequest; }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public boolean isFromSupporter() { return fromSupporter; }
    public void setFromSupporter(boolean fromSupporter) { this.fromSupporter = fromSupporter; }
}