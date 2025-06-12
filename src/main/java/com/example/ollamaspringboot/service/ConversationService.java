package com.example.ollamaspringboot.service;

import com.example.ollamaspringboot.model.ConversationMessage;
import com.example.ollamaspringboot.model.Discussion;
import com.example.ollamaspringboot.repository.ConversationMessageRepository;
import com.example.ollamaspringboot.repository.DiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationMessageRepository conversationMessageRepository;
    
    @Autowired
    private DiscussionRepository discussionRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHistory(String discussionId) {
        return conversationMessageRepository.findByDiscussion_DiscussionIdOrderByTimestampAsc(discussionId).stream()
                .map(msg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("role", msg.getRole());
                    map.put("content", msg.getContent());
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public String getDiscussionName(String discussionId) {
        return discussionRepository.findByDiscussionId(discussionId)
                .map(Discussion::getName)
                .orElse("Nouvelle discussion");
    }

    @Transactional
    public void addMessage(String userId, String discussionId, Map<String, Object> message) {
        // Trouver ou créer la discussion
        Discussion discussion = discussionRepository.findByDiscussionId(discussionId)
                .orElseGet(() -> {
                    Discussion newDiscussion = new Discussion();
                    newDiscussion.setDiscussionId(discussionId);
                    newDiscussion.setUserId(userId);
                    
                    // Créer un nom de discussion basé sur le premier message
                    String content = (String) message.get("content");
                    String discussionName = content.length() > 50 
                            ? content.substring(0, 47) + "..." 
                            : content;
                    newDiscussion.setName(discussionName);
                    
                    return discussionRepository.save(newDiscussion);
                });
        
        // Créer et sauvegarder le message
        ConversationMessage conversationMessage = new ConversationMessage();
        conversationMessage.setUserId(userId);
        conversationMessage.setDiscussion(discussion);
        conversationMessage.setRole((String) message.get("role"));
        conversationMessage.setContent((String) message.get("content"));
        conversationMessageRepository.save(conversationMessage);
    }
}
