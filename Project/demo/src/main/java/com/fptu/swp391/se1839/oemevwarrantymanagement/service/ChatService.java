package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.util.List;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ConversationResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Conversation;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Message;

public interface ChatService {
    Conversation startConversation(Long staffId, Long technicianId);

    Message sendMessage(Long conversationId, Long senderId, String content);

    List<Message> getMessagesByConversation(Long conversationId);

    List<ConversationResponse> getConversationsByUser(
            Long userId);

}
