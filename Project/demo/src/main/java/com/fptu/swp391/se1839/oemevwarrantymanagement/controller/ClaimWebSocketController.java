package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.WarrantyClaim;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ClaimWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public ClaimWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // client gửi lên server khi có hành động nào đó
    @MessageMapping("/claim/update")
    public void sendClaimUpdate(WarrantyClaim claim) {
        // gửi message tới tất cả client đang subscribe topic /topic/claims
        messagingTemplate.convertAndSend("/topic/claims", claim);
    }
}
