package com.codecompass.backend.controller;

import com.codecompass.backend.dto.*;
import com.codecompass.backend.entity.ConversationEntity;
import com.codecompass.backend.entity.MessageEntity;
import com.codecompass.backend.entity.UserEntity;
import com.codecompass.backend.repository.CodeChunkRepository;
import com.codecompass.backend.repository.ConversationRepository;
import com.codecompass.backend.repository.MessageRepository;
import com.codecompass.backend.repository.UserRepository;
import com.codecompass.backend.service.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@RestController
public class RepoController {

    private final GitCloneService gitCloneService;
    private final FileFilterService fileFilterService;
    private final JavaChunkerService javaChunkerService;
    private final EmbeddingService embeddingService;
    private final ChunkStorageService chunkStorageService;
    private final CodeChunkRepository codeChunkRepository;
    private final RetrievalService retrievalService;
    private final LlmService llmService;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public RepoController(GitCloneService gitCloneService, FileFilterService fileFilterService,
                          JavaChunkerService javaChunkerService, EmbeddingService embeddingService,
                          ChunkStorageService chunkStorageService, CodeChunkRepository codeChunkRepository,
                          RetrievalService retrievalService, LlmService llmService,
                          UserRepository userRepository, ConversationRepository conversationRepository,
                          MessageRepository messageRepository) {
        this.gitCloneService = gitCloneService;
        this.fileFilterService = fileFilterService;
        this.javaChunkerService = javaChunkerService;
        this.embeddingService = embeddingService;
        this.chunkStorageService = chunkStorageService;
        this.codeChunkRepository = codeChunkRepository;
        this.retrievalService = retrievalService;
        this.llmService = llmService;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @PostMapping("/api/repos/index")
    public RepoIndexResult indexRepo(@RequestBody RepoRequest request) throws Exception {
        String repoUrl = request.getRepoUrl();

        if (codeChunkRepository.existsByRepoUrl(repoUrl)) {
            codeChunkRepository.deleteByRepoUrl(repoUrl);
        }

        String clonedPath = gitCloneService.cloneRepo(repoUrl);
        List<Path> sourceFiles = fileFilterService.listSourceFiles(clonedPath);
        Set<String> allExtensions = fileFilterService.detectAllExtensions(clonedPath);

        int totalChunksSaved = 0;

        for (Path file : sourceFiles) {
            if (file.toString().endsWith(".java")) {
                List<CodeChunk> chunks = javaChunkerService.chunkFile(file);
                for (CodeChunk chunk : chunks) {
                    List<Double> embedding = embeddingService.embed(chunk.getContent());
                    chunkStorageService.saveChunk(repoUrl, chunk, embedding);
                    totalChunksSaved++;
                }
            }
        }

        String message = totalChunksSaved == 0
                ? "No supported source files found."
                : "Successfully indexed " + totalChunksSaved + " chunks from " + sourceFiles.size() + " files.";

        return new RepoIndexResult(
                sourceFiles.stream().map(Path::toString).toList(),
                allExtensions,
                message
        );
    }

    @PostMapping("/api/repos")
    public RepoIndexResult submitRepo(@RequestBody RepoRequest request) throws Exception {
        String clonedPath = gitCloneService.cloneRepo(request.getRepoUrl());
        List<Path> sourceFiles = fileFilterService.listSourceFiles(clonedPath);
        Set<String> allExtensions = fileFilterService.detectAllExtensions(clonedPath);
        List<String> filePaths = sourceFiles.stream().map(Path::toString).toList();
        String message = sourceFiles.isEmpty()
                ? "No supported source files found. CodeCompass currently supports Java and JS/TS/React (MERN) projects only."
                : "Found " + sourceFiles.size() + " supported source file(s).";
        return new RepoIndexResult(filePaths, allExtensions, message);
    }

    @PostMapping("/api/repos/chunk-test")
    public List<CodeChunk> chunkTest(@RequestBody RepoRequest request) throws Exception {
        String clonedPath = gitCloneService.cloneRepo(request.getRepoUrl());
        List<Path> sourceFiles = fileFilterService.listSourceFiles(clonedPath);
        List<CodeChunk> allChunks = new java.util.ArrayList<>();
        for (Path file : sourceFiles) {
            if (file.toString().endsWith(".java")) {
                allChunks.addAll(javaChunkerService.chunkFile(file));
            }
        }
        return allChunks;
    }

    @PostMapping("/api/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ConversationEntity conversation;
        if (request.getConversationId() == null) {
            conversation = new ConversationEntity(user.getId(), request.getRepoUrl(), request.getQuestion());
            conversation = conversationRepository.save(conversation);
        } else {
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            if (!conversation.getUserId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to access this conversation");
            }
        }

        List<ChunkSearchResult> chunks = retrievalService.findRelevantChunks(
                request.getRepoUrl(),
                request.getQuestion(),
                5
        );

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are a helpful code assistant. A student is exploring a GitHub repository ")
                .append("and has asked a question about the codebase.\n\n")
                .append("Here are the most relevant pieces of code:\n\n");

        for (ChunkSearchResult chunk : chunks) {
            promptBuilder.append("[").append(chunk.getChunkType()).append(": ")
                    .append(chunk.getName()).append(" in ").append(chunk.getFilePath())
                    .append(", lines ").append(chunk.getStartLine()).append("-").append(chunk.getEndLine())
                    .append("]\n")
                    .append(chunk.getContent())
                    .append("\n\n");
        }

        promptBuilder.append("Student's question: \"").append(request.getQuestion()).append("\"\n\n")
                .append("Answer based on the code above. Be specific, reference actual method names ")
                .append("and files, explain clearly for a student who is learning.");

        String answer = llmService.generateAnswer(promptBuilder.toString());

        List<ChatResponse.Citation> citations = chunks.stream()
                .map(chunk -> new ChatResponse.Citation(
                        chunk.getFilePath(),
                        chunk.getName(),
                        chunk.getStartLine(),
                        chunk.getEndLine()
                ))
                .toList();

        String citationsJson = citationsToJson(citations);
        messageRepository.save(new MessageEntity(conversation.getId(), "user", request.getQuestion(), null));
        messageRepository.save(new MessageEntity(conversation.getId(), "assistant", answer, citationsJson));

        return new ChatResponse(answer, citations, conversation.getId());
    }

    private String citationsToJson(List<ChatResponse.Citation> citations) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < citations.size(); i++) {
            ChatResponse.Citation c = citations.get(i);
            json.append("{\"filePath\":\"").append(c.filePath().replace("\\", "\\\\")).append("\",")
                    .append("\"name\":\"").append(c.name()).append("\",")
                    .append("\"startLine\":").append(c.startLine()).append(",")
                    .append("\"endLine\":").append(c.endLine()).append("}");
            if (i < citations.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    @PostMapping("/api/search")
    public List<String> search(@RequestBody SearchRequest request) {
        List<ChunkSearchResult> chunks = retrievalService.findRelevantChunks(
                request.getRepoUrl(),
                request.getQuestion(),
                5
        );

        return chunks.stream()
                .map(chunk -> "[" + chunk.getChunkType() + "] "
                        + chunk.getName()
                        + " (" + chunk.getFilePath().substring(chunk.getFilePath().lastIndexOf("\\") + 1) + ")"
                        + " lines " + chunk.getStartLine() + "-" + chunk.getEndLine())
                .toList();
    }
}