package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepairHistoryResponse {
    private Long orderId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean supervisorApproved;
    private String claimDescription;
    private int claimMileage;
    private List<RepairDetailHistoryResponse> details;
}
