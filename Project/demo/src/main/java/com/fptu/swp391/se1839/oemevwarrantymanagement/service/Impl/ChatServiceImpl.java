package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ConversationResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.MessageResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.UserResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Conversation;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Message;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.ConversationRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.MessageRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.UserRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ChatService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatServiceImpl implements ChatService {
    final ConversationRepository conversationRepository;

    final ConversationRepository conversationRepo;
    final MessageRepository messageRepository;
    final UserRepository userRepository;

    @Override
    public Conversation startConversation(Long staffId, Long technicianId) {

        return conversationRepo.findByStaffIdAndTechnicianId(staffId, technicianId)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setStaffId(staffId);
                    c.setTechnicianId(technicianId);
                    return conversationRepo.save(c);
                });
    }

    @Override
    public Message sendMessage(Long conversationId, Long senderId, String content) {
        Conversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setSenderId(senderId);
        msg.setContent(content);
        msg.setTimestamp(System.currentTimeMillis());

        return messageRepository.save(msg);
    }

    @Override
    public List<Message> getMessagesByConversation(Long conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }

    @Override
    public List<ConversationResponse> getConversationsByUser(Long userId) {
        List<Conversation> convs = conversationRepository.findDistinctByUser(userId);

        return convs.stream()
                .map(c -> {
                    // fetch staff + technician
                    User staffUser = (c.getStaffId() != null)
                            ? userRepository.findById(c.getStaffId()).orElse(null)
                            : null;

                    User techUser = (c.getTechnicianId() != null)
                            ? userRepository.findById(c.getTechnicianId()).orElse(null)
                            : null;

                    UserResponse staffDto = staffUser != null ? UserResponse.fromEntity(staffUser) : null;
                    UserResponse techDto = techUser != null ? UserResponse.fromEntity(techUser) : null;

                    List<Message> msgs = messageRepository.findByConversationIdOrderByTimestampAsc(c.getId());
                    MessageResponse last = msgs.isEmpty() ? null
                            : MessageResponse.fromEntity(msgs.get(msgs.size() - 1));

                    return ConversationResponse.ofEntity(c, staffDto, techDto, last);
                })
                .collect(java.util.stream.Collectors.toList());

    }

}
