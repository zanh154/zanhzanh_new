package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.SendMessageRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.MessageResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Message;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ChatService;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.security.oauth2.jwt.Jwt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class MessageController {

    final ChatService chatService;

    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody SendMessageRequest req) {

        Long senderId = Long.parseLong(jwt.getClaim("userId").toString());

        Message msg = chatService.sendMessage(
                req.getConversationId(),
                senderId,
                req.getMessage());

        return ResponseEntity.ok(MessageResponse.fromEntity(msg));
    }

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<?> getMessages(
            @PathVariable Long conversationId) {

        List<Message> msgs = chatService.getMessagesByConversation(conversationId);

        List<MessageResponse> response = msgs.stream()
                .map(MessageResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

}
