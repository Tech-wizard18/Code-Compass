package com.codecompass.backend.dto;

import java.time.LocalDateTime;

public class ConversationSummary {

    private Long id;
    private String repoUrl;
    private String title;
    private LocalDateTime createdAt;

    public ConversationSummary(Long id, String repoUrl, String title, LocalDateTime createdAt) {
        this.id = id;
        this.repoUrl = repoUrl;
        this.title = title;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getRepoUrl() { return repoUrl; }
    public String getTitle() { return title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}