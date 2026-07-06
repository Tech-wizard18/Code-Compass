package com.codecompass.backend.dto;

public class ChatRequest {

    private String repoUrl;
    private String question;
    private Long conversationId; // null = start a new conversation

    public ChatRequest() {}

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}

