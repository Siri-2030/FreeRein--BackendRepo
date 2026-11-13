package com.hr.freerein.entity;

import jakarta.persistence.*;

/** One direct message between two users. conversationId = both emails, sorted, joined by "_". */
@Entity
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String senderEmail;

    @Column(nullable = false)
    private String receiverEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Boolean readFlag;

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getReceiverEmail() { return receiverEmail; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isRead() { return readFlag != null && readFlag; }
    public void setRead(Boolean read) { this.readFlag = read; }
}
