package com.codecompass.backend.dto;

import java.time.LocalDateTime;

public class MessageDto {

    private Long id;
    private String role;
    private String content;
    private String citations;
    private LocalDateTime createdAt;

    public MessageDto(Long id, String role, String content, String citations, LocalDateTime createdAt) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.citations = citations;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public String getCitations() { return citations; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}