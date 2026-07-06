package com.codecompass.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    // nomic-embed-text: ~8192 tokens, ~3 chars/token for code = ~24000 chars
    // We use slightly less to be safe
    private static final int CHUNK_SIZE = 2000;

    // Overlap between consecutive pieces — preserves context at boundaries
    private static final int OVERLAP = 200;

    private final RestClient restClient;

    public EmbeddingService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public List<Double> embed(String text) {
        // If text fits within limit — embed directly, single call
        System.out.println("DEBUG: text length = " + text.length() + ", CHUNK_SIZE = " + CHUNK_SIZE);
        if (text.length() <= CHUNK_SIZE) {
            return callOllama(text);
        }

        // Text is too big — split into overlapping pieces,
        // embed each piece, average the vectors together
        List<String> pieces = splitWithOverlap(text);
        List<List<Double>> allVectors = new ArrayList<>();

        for (String piece : pieces) {
            List<Double> vector = callOllama(piece);
            allVectors.add(vector);
        }

        // Average all piece vectors into one final vector
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

    private List<Double> callOllama(String text) {
        Map<String, Object> requestBody = Map.of(
                "model", "nomic-embed-text",
                "prompt", text
        );

        OllamaResponse response = restClient.post()
                .uri("/api/embeddings")
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(OllamaResponse.class);

        return response.embedding();
    }

    record OllamaResponse(@JsonProperty("embedding") List<Double> embedding) {}
}