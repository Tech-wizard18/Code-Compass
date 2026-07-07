package com.codecompass.backend.repository;

import com.codecompass.backend.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    void deleteByConversationId(Long conversationId);
}