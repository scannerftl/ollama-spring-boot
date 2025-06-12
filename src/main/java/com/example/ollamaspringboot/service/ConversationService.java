package com.example.ollamaspringboot.service;

import com.example.ollamaspringboot.model.ConversationMessage;
import com.example.ollamaspringboot.repository.ConversationMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationMessageRepository repository;

    public List<Map<String, Object>> getHistory(String userId) {
        return repository.findByUserIdOrderByTimestampAsc(userId).stream()
                .map(msg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("role", msg.getRole());
                    map.put("content", msg.getContent());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public void addMessage(String userId, Map<String, Object> message) {
        ConversationMessage conversationMessage = new ConversationMessage();
        conversationMessage.setUserId(userId);
        conversationMessage.setRole((String) message.get("role"));
        conversationMessage.setContent((String) message.get("content"));
        repository.save(conversationMessage);
    }
}
