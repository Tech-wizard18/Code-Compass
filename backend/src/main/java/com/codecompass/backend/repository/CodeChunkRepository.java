package com.codecompass.backend.repository;

import com.codecompass.backend.dto.ChunkSearchResult;
import com.codecompass.backend.entity.CodeChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CodeChunkRepository extends JpaRepository<CodeChunkEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM CodeChunkEntity c WHERE c.repoUrl = :repoUrl")
    void deleteByRepoUrl(String repoUrl);

    boolean existsByRepoUrl(String repoUrl);

    @Query(value = """
        SELECT id, repo_url, file_path, chunk_type, name, start_line, end_line, content
        FROM code_chunks
        WHERE repo_url = :repoUrl
        ORDER BY embedding <=> CAST(:embedding AS vector)
        LIMIT :topK
        """, nativeQuery = true)
    List<ChunkSearchResult> findSimilarChunks(
            @Param("repoUrl") String repoUrl,
            @Param("embedding") String embedding,
            @Param("topK") int topK
    );
}