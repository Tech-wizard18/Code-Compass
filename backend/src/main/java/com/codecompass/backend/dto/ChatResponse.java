package com.codecompass.backend.dto;

import java.util.List;

public class ChatResponse {

    private String answer;
    private List<Citation> citations;
    private Long conversationId;

    public ChatResponse() {}

    public ChatResponse(String answer, List<Citation> citations, Long conversationId) {
        this.answer = answer;
        this.citations = citations;
        this.conversationId = conversationId;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<Citation> getCitations() { return citations; }
    public void setCitations(List<Citation> citations) { this.citations = citations; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public record Citation(String filePath, String name, int startLine, int endLine) {}
}