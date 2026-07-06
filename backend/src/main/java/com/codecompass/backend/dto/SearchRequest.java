package com.codecompass.backend.dto;

public class SearchRequest {

    private String repoUrl;
    private String question;

    public SearchRequest() {}

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}