package com.example.ollamaspringboot.controller;

import com.example.ollamaspringboot.dto.DiscussionDTO;
import com.example.ollamaspringboot.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    /**
     * Récupère toutes les discussions d'un utilisateur
     * @param userId ID de l'utilisateur
     * @return Liste des discussions de l'utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DiscussionDTO>> getUserDiscussions(@PathVariable String userId) {
        List<DiscussionDTO> discussions = discussionService.getUserDiscussions(userId);
        return ResponseEntity.ok(discussions);
    }

    /**
     * Supprime une discussion
     * @param discussionId ID de la discussion à supprimer
     * @param userId ID de l'utilisateur (passé en paramètre de requête pour des raisons de sécurité)
     * @return Réponse vide avec statut 200 si la suppression a réussi
     */
    @DeleteMapping("/{discussionId}")
    public ResponseEntity<Void> deleteDiscussion(
            @PathVariable String discussionId,
            @RequestParam String userId) {
        
        discussionService.deleteDiscussion(discussionId, userId);
        return ResponseEntity.ok().build();
    }
}
