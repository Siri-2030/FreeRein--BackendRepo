package com.hr.freerein.controller;

import com.hr.freerein.dto.ApiResponse;
import com.hr.freerein.dto.Requests.ChatSend;
import com.hr.freerein.entity.ChatMessage;
import com.hr.freerein.entity.User;
import com.hr.freerein.exception.ResourceNotFoundException;
import com.hr.freerein.repository.ChatMessageRepository;
import com.hr.freerein.repository.UserRepository;
import com.hr.freerein.service.AccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Direct messages between two users. conversationId = the two emails sorted and joined with "_".
 * Rules: you only ever see conversations you take part in, and a victim can only write to staff.
 */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "Direct messages between a survivor and a counsellor / legal advisor.")
public class ChatController {

    private final ChatMessageRepository messages;
    private final UserRepository users;
    private final AccessService access;

    public ChatController(ChatMessageRepository messages, UserRepository users, AccessService access) {
        this.messages = messages;
        this.users = users;
        this.access = access;
    }

    static String conversationId(String a, String b) {
        String x = a.toLowerCase();
        String y = b.toLowerCase();
        return x.compareTo(y) <= 0 ? x + "_" + y : y + "_" + x;
    }

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> history(@PathVariable String conversationId, Authentication auth) {
        User me = access.currentUser(auth);
        List<ChatMessage> list = messages.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
                .filter(m -> me.getEmail().equalsIgnoreCase(m.getSenderEmail())
                        || me.getEmail().equalsIgnoreCase(m.getReceiverEmail()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list, "Messages retrieved"));
    }

    @PostMapping("/messages/{conversationId}")
    @Operation(summary = "Send a message")
    public ResponseEntity<ApiResponse<ChatMessage>> send(@PathVariable String conversationId,
                                                         @Valid @RequestBody ChatSend req, Authentication auth) {
        User me = access.currentUser(auth);
        String receiverEmail = req.receiverEmail().trim().toLowerCase();
        User receiver = users.findByEmail(receiverEmail).filter(User::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        if (receiver.getId().equals(me.getId())) {
            throw new IllegalArgumentException("You cannot message yourself");
        }
        if (!access.isStaff(me) && !access.isStaff(receiver)) {
            throw new AccessDeniedException("Survivors can only message counsellors, legal advisors or admins");
        }
        if (!conversationId(me.getEmail(), receiver.getEmail()).equals(conversationId)) {
            throw new IllegalArgumentException("conversationId does not match the participants");
        }

        ChatMessage m = new ChatMessage();
        m.setConversationId(conversationId);
        m.setSenderEmail(me.getEmail());
        m.setReceiverEmail(receiver.getEmail());
        m.setContent(req.content().trim());
        m.setRead(false);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(messages.save(m)));
    }

    @GetMapping("/conversations")
    @Operation(summary = "Latest message of each of my conversations")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> conversations(Authentication auth) {
        User me = access.currentUser(auth);
        Map<String, ChatMessage> latest = new LinkedHashMap<>();
        for (ChatMessage m : messages.findBySenderEmailOrReceiverEmailOrderByCreatedAtDesc(me.getEmail(), me.getEmail())) {
            latest.putIfAbsent(m.getConversationId(), m);   // list is newest first
        }
        return ResponseEntity.ok(ApiResponse.success(new ArrayList<>(latest.values()), "Conversations retrieved"));
    }

    @PatchMapping("/messages/{id}/read")
    public ResponseEntity<ApiResponse<ChatMessage>> markRead(@PathVariable Long id, Authentication auth) {
        User me = access.currentUser(auth);
        ChatMessage m = messages.findById(id)
                .filter(x -> me.getEmail().equalsIgnoreCase(x.getReceiverEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + id));
        m.setRead(true);
        return ResponseEntity.ok(ApiResponse.updated(messages.save(m)));
    }
}
