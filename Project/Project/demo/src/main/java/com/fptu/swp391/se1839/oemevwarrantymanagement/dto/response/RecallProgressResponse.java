package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecallProgressResponse {
    private Long id;
    private Long campaignId;
    private String campaignName;
    private String campaignCode;
    private String vehicleVin;
    private String vehicleModel;
    private String customerName;
    private String customerPhone;
    private String status;
    private LocalDateTime notificationDate;
    private LocalDateTime completionDate;
    private String notes;
}