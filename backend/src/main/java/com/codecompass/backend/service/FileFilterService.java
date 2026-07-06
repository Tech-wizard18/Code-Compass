package com.codecompass.backend.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileFilterService {

    private static final Set<String> IGNORED_FOLDERS = Set.of(
            ".git", "node_modules", "target", "build", "dist", ".idea", ".vscode"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".java", ".js", ".jsx", ".ts", ".tsx"
    );

    private static final Set<String> SUSPICIOUS_NAME_PARTS = Set.of(
            ".env", "secret", "credential", "password", "apikey", "api_key", ".pem", ".key"
    );

    public List<Path> listSourceFiles(String rootPath) throws IOException {
        Path root = Path.of(rootPath);
        try (Stream<Path> walk = Files.walk(root)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(this::isInsideAllowedFolder)
                    .filter(this::hasAllowedExtension)
                    .filter(this::isNotSuspicious)
                    .collect(Collectors.toList());
        }
    }

    public Set<String> detectAllExtensions(String rootPath) throws IOException {
        Path root = Path.of(rootPath);
        try (Stream<Path> walk = Files.walk(root)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(this::isInsideAllowedFolder)
                    .filter(this::isNotSuspicious)
                    .map(this::getExtension)
                    .filter(ext -> !ext.isEmpty())
                    .collect(Collectors.toSet());
        }
    }

    private String getExtension(Path path) {
        String name = path.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        return lastDot == -1 ? "" : name.substring(lastDot).toLowerCase();
    }

    private boolean isInsideAllowedFolder(Path path) {
        for (Path part : path) {
            if (IGNORED_FOLDERS.contains(part.toString())) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAllowedExtension(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return ALLOWED_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private boolean isNotSuspicious(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return SUSPICIOUS_NAME_PARTS.stream().noneMatch(name::contains);
    }
}