package com.codecompass.backend.dto;

public interface ChunkSearchResult {
    Long getId();
    String getRepoUrl();
    String getFilePath();
    String getChunkType();
    String getName();
    int getStartLine();
    int getEndLine();
    String getContent();
}