package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    Long id;
    Long senderId;
    String content;
    Long timestamp;

    public static MessageResponse fromEntity(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .content(m.getContent())
                .timestamp(m.getTimestamp())
                .build();
    }
}
