package com.example.ollamaspringboot.repository;

import com.example.ollamaspringboot.model.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    @Query("SELECT m FROM ConversationMessage m JOIN m.discussion d WHERE d.discussionId = :discussionId ORDER BY m.timestamp ASC")
    List<ConversationMessage> findByDiscussion_DiscussionIdOrderByTimestampAsc(@Param("discussionId") String discussionId);
    
    @Query("SELECT COUNT(m) FROM ConversationMessage m WHERE m.discussion.id = :discussionId")
    int countByDiscussionId(@Param("discussionId") Long discussionId);
}
