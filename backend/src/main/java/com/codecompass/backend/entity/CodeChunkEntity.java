package com.codecompass.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name="code_chunks")
public class CodeChunkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "chunk_type", nullable = false)
    private String chunkType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_line", nullable = false)
    private int startLine;

    @Column(name = "end_line", nullable = false)
    private int endLine;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "embedding", columnDefinition = "vector(768)", insertable = true, updatable = false)
    @org.hibernate.annotations.Formula("null")
    private float[] embedding;

    public CodeChunkEntity() {}

    public CodeChunkEntity(String repoUrl, String filePath, String chunkType, String name, int startLine, int endLine, String content) {
        this.repoUrl = repoUrl;
        this.filePath = filePath;
        this.chunkType = chunkType;
        this.name = name;
        this.startLine = startLine;
        this.endLine = endLine;
        this.content = content;

    }

    public Long getId() { return id; }
    public String getRepoUrl() { return repoUrl; }
    public String getFilePath() { return filePath; }
    public String getChunkType() { return chunkType; }
    public String getName() { return name; }
    public int getStartLine() { return startLine; }
    public int getEndLine() { return endLine; }
    public String getContent() { return content; }

}
