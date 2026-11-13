package com.hr.freerein.repository;

import com.hr.freerein.entity.ChatMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends BaseRepository<ChatMessage, Long> {

    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    List<ChatMessage> findBySenderEmailOrReceiverEmailOrderByCreatedAtDesc(String senderEmail, String receiverEmail);
}
