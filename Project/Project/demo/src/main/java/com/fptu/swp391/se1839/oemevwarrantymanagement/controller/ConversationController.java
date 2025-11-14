package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.StartConversationRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ConversationResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.StartConversationResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Conversation;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ChatService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class ConversationController {

    final ChatService chatService;

    @PostMapping("/conversations/start")
    public ResponseEntity<?> startConversation(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody StartConversationRequest req) {

        Long staffId = Long.parseLong(jwt.getClaim("userId").toString());

        Conversation conv = chatService.startConversation(staffId, req.getTechnicianId());

        return ResponseEntity.ok(new StartConversationResponse(conv.getId()));
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getMyConversations(
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());
        List<ConversationResponse> convs = chatService.getConversationsByUser(userId);

        var res = ApiResponse.<List<ConversationResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Get conversations successfully")
                .data(convs)
                .build();

        return ResponseEntity.ok(res);
    }

}
