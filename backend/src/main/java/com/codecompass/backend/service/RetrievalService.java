package com.codecompass.backend.service;

import com.codecompass.backend.dto.ChunkSearchResult;
import com.codecompass.backend.repository.CodeChunkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievalService {

    private final CodeChunkRepository repository;
    private final EmbeddingService embeddingService;

    public RetrievalService(CodeChunkRepository repository, EmbeddingService embeddingService) {
        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    public List<ChunkSearchResult> findRelevantChunks(String repoUrl, String question, int topK) {
        List<Double> questionVector = embeddingService.embed(question);
        String embeddingString = toVectorString(questionVector);
        return repository.findSimilarChunks(repoUrl, embeddingString, topK);
    }

    private String toVectorString(List<Double> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.size(); i++) {
            sb.append(vector.get(i));
            if (i < vector.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}