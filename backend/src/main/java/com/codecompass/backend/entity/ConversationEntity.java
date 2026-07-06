package com.codecompass.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    private String title;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ConversationEntity() {}

    public ConversationEntity(Long userId, String repoUrl, String title) {
        this.userId = userId;
        this.repoUrl = repoUrl;
        this.title = title;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getRepoUrl() { return repoUrl; }
    public String getTitle() { return title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}