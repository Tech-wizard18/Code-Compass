package com.codecompass.backend.dto;

public class CodeChunk {

    private String filePath;
    private String chunkType;
    private String name;
    private int startLine;
    private int endLine;
    private String content;

    public CodeChunk(String filePath, String chunkType, String name, int startLine, int endLine, String content) {
        this.filePath = filePath;
        this.chunkType = chunkType;
        this.name = name;
        this.startLine = startLine;
        this.endLine = endLine;
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getChunkType() {
        return chunkType;
    }

    public String getName() {
        return name;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public String getContent() {
        return content;
    }
}