package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePartResponse {
    private String id;
    private String oldSerialNumber;
    private String partName;
    private String partCategory;
    private String location; // vị trí trên xe (nếu có)
    private Boolean isSelected; // đã được gắn vào claim nào chưa
    private Long currentClaimId; // ID của claim đang dùng part này (nếu có)
}