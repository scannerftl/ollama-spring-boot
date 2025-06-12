package com.example.ollamaspringboot.repository;

import com.example.ollamaspringboot.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    Optional<Discussion> findByDiscussionId(String discussionId);
    List<Discussion> findByUserIdOrderByCreatedAtDesc(String userId);
}
