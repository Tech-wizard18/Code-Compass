package com.codecompass.backend.dto;

import java.util.List;
import java.util.Set;

public class RepoIndexResult {

    private List<String> supportedFiles;
    private Set<String> allExtensionsFound;
    private String message;

    public RepoIndexResult(List<String> supportedFiles, Set<String> allExtensionsFound, String message) {
        this.supportedFiles = supportedFiles;
        this.allExtensionsFound = allExtensionsFound;
        this.message = message;
    }

    public List<String> getSupportedFiles() {
        return supportedFiles;
    }

    public Set<String> getAllExtensionsFound() {
        return allExtensionsFound;
    }

    public String getMessage() {
        return message;
    }
}