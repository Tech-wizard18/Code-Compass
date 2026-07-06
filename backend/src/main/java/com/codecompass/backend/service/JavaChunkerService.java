package com.codecompass.backend.service;

import com.codecompass.backend.dto.CodeChunk;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class JavaChunkerService {

    private final JavaParser javaParser;

    public JavaChunkerService() {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        this.javaParser = new JavaParser(config);
    }

    public List<CodeChunk> chunkFile(Path filePath) throws IOException {
        List<CodeChunk> chunks = new ArrayList<>();

        String fileContent = Files.readString(filePath);
        ParseResult<CompilationUnit> result = javaParser.parse(fileContent);

        if (!result.isSuccessful() || result.getResult().isEmpty()) {
            // This file couldn't be parsed (unsupported syntax, etc.) — skip it safely
            return chunks;
        }

        CompilationUnit cu = result.getResult().get();

        List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

        for (ClassOrInterfaceDeclaration clazz : classes) {
            String name = clazz.getNameAsString();
            int startLine = clazz.getBegin().get().line;
            int endLine = clazz.getEnd().get().line;
            String content = clazz.toString();

            chunks.add(new CodeChunk(filePath.toString(), "CLASS", name, startLine, endLine, content));
        }

        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

        for (MethodDeclaration method : methods) {
            String name = method.getNameAsString();
            int startLine = method.getBegin().get().line;
            int endLine = method.getEnd().get().line;
            String content = method.toString();

            chunks.add(new CodeChunk(filePath.toString(), "METHOD", name, startLine, endLine, content));
        }

        return chunks;
    }
}