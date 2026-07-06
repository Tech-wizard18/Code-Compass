package com.codecompass.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String citations;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MessageEntity() {}

    public MessageEntity(Long conversationId, String role, String content, String citations) {
        this.conversationId = conversationId;
        this.role = role;
        this.content = content;
        this.citations = citations;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getConversationId() { return conversationId; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public String getCitations() { return citations; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}