package com.codecompass.backend.repository;

import com.codecompass.backend.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {

    List<ConversationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}