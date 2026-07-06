package com.codecompass.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@Service
public class LlmService {

    private final RestClient restClient;

    public LlmService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generateAnswer(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        OllamaGenerateResponse response = restClient.post()
                .uri("/api/generate")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(OllamaGenerateResponse.class);

        return response.response();
    }

    record OllamaGenerateResponse(@JsonProperty("response") String response) {}
}