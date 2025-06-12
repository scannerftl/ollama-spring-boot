package com.example.ollamaspringboot.repository;

import com.example.ollamaspringboot.model.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    List<ConversationMessage> findByDiscussionIdOrderByTimestampAsc(String discussionId);
}
