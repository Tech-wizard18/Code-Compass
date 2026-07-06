package com.codecompass.backend.service;

import com.codecompass.backend.dto.CodeChunk;
import com.codecompass.backend.entity.CodeChunkEntity;
import com.codecompass.backend.repository.CodeChunkRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChunkStorageService {

    private final CodeChunkRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    public ChunkStorageService(CodeChunkRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void saveChunk(String repoUrl, CodeChunk chunk, List<Double> embedding) {
        // Convert embedding to pgvector string format
        String embeddingString = toVectorString(embedding);

        // Use native SQL to insert with the vector type
        entityManager.createNativeQuery(
                        "INSERT INTO code_chunks (repo_url, file_path, chunk_type, name, start_line, end_line, content, embedding) " +
                                "VALUES (:repoUrl, :filePath, :chunkType, :name, :startLine, :endLine, :content, CAST(:embedding AS vector))"
                )
                .setParameter("repoUrl", repoUrl)
                .setParameter("filePath", chunk.getFilePath())
                .setParameter("chunkType", chunk.getChunkType())
                .setParameter("name", chunk.getName())
                .setParameter("startLine", chunk.getStartLine())
                .setParameter("endLine", chunk.getEndLine())
                .setParameter("content", chunk.getContent())
                .setParameter("embedding", embeddingString)
                .executeUpdate();
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