package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.*;
import java.util.Optional;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Conversation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationResponse {
    private Long id;
    private Long staffId;
    private Long technicianId;
    private UserResponse staff; // reuse your UserResponse DTO
    private UserResponse technician;
    private MessageResponse lastMessage;

    public static ConversationResponse ofEntity(
            Conversation c,
            UserResponse staffDto,
            UserResponse techDto,
            MessageResponse lastMsg) {
        return ConversationResponse.builder()
                .id(c.getId())
                .staffId(c.getStaffId())
                .technicianId(c.getTechnicianId())
                .staff(staffDto)
                .technician(techDto)
                .lastMessage(lastMsg)
                .build();
    }
}