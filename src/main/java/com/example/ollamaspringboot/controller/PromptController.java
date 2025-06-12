package com.example.ollamaspringboot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.ollamaspringboot.dto.PromptRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.ollamaspringboot.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PromptController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ConversationService conversationService;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @PostMapping("/prompt")
    public Map<String, String> prompt(@RequestBody PromptRequest promptRequest) {
        if (groqApiKey == null || groqApiKey.equals("YOUR_GROQ_API_KEY_HERE") || groqApiKey.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Groq API key is not configured. Please set 'groq.api.key' in application.properties.");
            return errorResponse;
        }

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); 
        headers.setBearerAuth(groqApiKey);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        // Create the request body for Groq Chat Completions API
        // Get conversation history
        List<Map<String, Object>> messages = conversationService.getHistory(promptRequest.getUserId());

        // Add the new user message to the history
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", promptRequest.getPrompt());
        messages.add(userMessage);
        conversationService.addMessage(promptRequest.getUserId(), userMessage);

        Map<String, Object> requestBody = new HashMap<>();
        String modelToUse = promptRequest.getModel() != null ? promptRequest.getModel() : groqModel;
        requestBody.put("model", modelToUse);
        requestBody.put("messages", messages);

        // Create the request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Call the Groq API
        @SuppressWarnings("unchecked")
        Map<String, Object> groqResponse = restTemplate.postForObject(groqApiUrl, requestEntity, Map.class);

        // Extract the response text from the complex JSON structure
        String responseText = "Error: Could not parse response from Groq API.";
        if (groqResponse != null && groqResponse.containsKey("choices")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) groqResponse.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                if (firstChoice.containsKey("message")) {
                    Object messageObj = firstChoice.get("message");
                    if (messageObj instanceof Map) {
                        @SuppressWarnings("unchecked") // For casting Object to Map<String, Object> after 'instanceof Map' check
                        Map<String, Object> responseMessage = (Map<String, Object>) messageObj;
                        if (responseMessage.containsKey("content")) {
                            Object contentObj = responseMessage.get("content");
                            if (contentObj instanceof String) {
                                responseText = (String) contentObj;
                                // Save assistant's response to history
                                Map<String, Object> assistantMessage = new HashMap<>();
                                assistantMessage.put("role", "assistant");
                                assistantMessage.put("content", responseText);
                                conversationService.addMessage(promptRequest.getUserId(), assistantMessage);
                            } else if (contentObj != null) {
                                System.err.println("Warning: Groq API response 'content' was not a String. Found: " + contentObj.getClass().getName());
                            }
                        }
                    } else if (messageObj != null) {
                        System.err.println("Warning: Groq API response 'message' was not a Map. Found: " + messageObj.getClass().getName());
                    }
                }
            }
        }

        // Return the response
        Map<String, String> response = new HashMap<>();
        response.put("response", responseText);
        return response;
    }
}
