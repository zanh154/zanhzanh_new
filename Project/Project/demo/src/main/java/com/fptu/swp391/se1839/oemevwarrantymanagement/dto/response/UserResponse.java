package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import com.fptu.swp391.se1839.oemevwarrantymanagement.entity.User;

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
public class UserResponse {
    Long id;
    String email;
    String name;
    String phoneNumber;
    User.Role role; // dùng luôn enum của entity
    User.Status status;
    Long serviceCenterId;

    public static UserResponse fromEntity(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .role(u.getRole()) // ✅ đúng kiểu
                .status(u.getStatus())
                .serviceCenterId(u.getServiceCenter() != null ? u.getServiceCenter().getId() : null)
                .build();
    }
}
