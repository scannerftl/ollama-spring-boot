package com.example.ollamaspringboot.service;

import com.example.ollamaspringboot.dto.DiscussionDTO;
import com.example.ollamaspringboot.model.Discussion;
import com.example.ollamaspringboot.repository.ConversationMessageRepository;
import com.example.ollamaspringboot.repository.DiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscussionService {

    @Autowired
    private DiscussionRepository discussionRepository;
    
    @Autowired
    private ConversationMessageRepository messageRepository;

    @Transactional(readOnly = true)
    public List<DiscussionDTO> getUserDiscussions(String userId) {
        return discussionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDiscussion(String discussionId, String userId) {
        Discussion discussion = discussionRepository.findByDiscussionId(discussionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion non trouvée"));
        
        if (!discussion.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non autorisé à supprimer cette discussion");
        }
        
        // Suppression en cascade des messages via la relation
        discussionRepository.delete(discussion);
    }

    private DiscussionDTO convertToDTO(Discussion discussion) {
        DiscussionDTO dto = new DiscussionDTO();
        dto.setId(discussion.getDiscussionId());
        dto.setName(discussion.getName());
        dto.setCreatedAt(discussion.getCreatedAt());
        
        // Compter les messages de la discussion
        int messageCount = messageRepository.countByDiscussionId(discussion.getId());
        dto.setMessageCount(messageCount);
        
        return dto;
    }
}
