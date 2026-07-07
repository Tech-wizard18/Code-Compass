package com.codecompass.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private final RestClient restClient;
    private final String apiKey;

    public LlmService(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String generateAnswer(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        GeminiGenerateResponse response = restClient.post()
                .uri("/v1beta/models/gemini-2.5-flash:generateContent")
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .body(requestBody)
                .retrieve()
                .body(GeminiGenerateResponse.class);

        return response.candidates().get(0).content().parts().get(0).text();
    }

    record GeminiGenerateResponse(@JsonProperty("candidates") List<Candidate> candidates) {}
    record Candidate(@JsonProperty("content") Content content) {}
    record Content(@JsonProperty("parts") List<Part> parts) {}
    record Part(@JsonProperty("text") String text) {}
}