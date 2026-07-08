package com.codecompass.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private static final int CHUNK_SIZE = 2000;
    private static final int OVERLAP = 200;

    private final RestClient restClient;
    private final String apiKey;

    public EmbeddingService(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public List<Double> embed(String text) {
        System.out.println("DEBUG: text length = " + text.length() + ", CHUNK_SIZE = " + CHUNK_SIZE);
        if (text.length() <= CHUNK_SIZE) {
            return callGemini(text);
        }

        List<String> pieces = splitWithOverlap(text);
        List<List<Double>> allVectors = new ArrayList<>();

        for (String piece : pieces) {
            List<Double> vector = callGemini(piece);
            allVectors.add(vector);
        }

        return averageVectors(allVectors);
    }

    private List<String> splitWithOverlap(String text) {
        List<String> pieces = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());
            pieces.add(text.substring(start, end));
            start += CHUNK_SIZE - OVERLAP;
        }

        return pieces;
    }

    private List<Double> averageVectors(List<List<Double>> vectors) {
        int dimensions = vectors.get(0).size();
        List<Double> averaged = new ArrayList<>();

        for (int i = 0; i < dimensions; i++) {
            double sum = 0;
            for (List<Double> vector : vectors) {
                sum += vector.get(i);
            }
            averaged.add(sum / vectors.size());
        }

        return averaged;
    }

    private List<Double> callGemini(String text) {
        Map<String, Object> requestBody = Map.of(
                "content", Map.of(
                        "parts", List.of(
                                Map.of("text", text)
                        )
                ),
                "outputDimensionality", 768
        );

        GeminiEmbedResponse response = restClient.post()
                .uri("/v1beta/models/gemini-embedding-001:embedContent")
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", apiKey)
                .body(requestBody)
                .retrieve()
                .body(GeminiEmbedResponse.class);

        return response.embedding().values();
    }

    record GeminiEmbedResponse(@JsonProperty("embedding") Embedding embedding) {}
    record Embedding(@JsonProperty("values") List<Double> values) {}
}