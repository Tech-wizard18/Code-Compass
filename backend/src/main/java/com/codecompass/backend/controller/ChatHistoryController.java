package com.codecompass.backend.controller;

import com.codecompass.backend.dto.ConversationSummary;
import com.codecompass.backend.dto.MessageDto;
import com.codecompass.backend.entity.ConversationEntity;
import com.codecompass.backend.entity.UserEntity;
import com.codecompass.backend.repository.ConversationRepository;
import com.codecompass.backend.repository.MessageRepository;
import com.codecompass.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatHistoryController {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatHistoryController(ConversationRepository conversationRepository,
                                 MessageRepository messageRepository,
                                 UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/conversations")
    public List<ConversationSummary> listConversations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return conversationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(c -> new ConversationSummary(c.getId(), c.getRepoUrl(), c.getTitle(), c.getCreatedAt()))
                .toList();
    }

    @GetMapping("/api/conversations/{id}/messages")
    public List<MessageDto> getConversationMessages(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUserId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to access this conversation");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(id)
                .stream()
                .map(m -> new MessageDto(m.getId(), m.getRole(), m.getContent(), m.getCitations(), m.getCreatedAt()))
                .toList();
    }

    @Transactional
    @DeleteMapping("/api/conversations/{id}")
    public Map<String, String> deleteConversation(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getUserId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to access this conversation");
        }

        messageRepository.deleteByConversationId(id);
        conversationRepository.delete(conversation);

        return Map.of("message", "Conversation deleted successfully.");
    }
}