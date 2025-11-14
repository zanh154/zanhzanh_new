package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInspectionResponse {
    private Long id;
    private Long warrantyClaimId;
    private String claimStatus;
    private Long inspectorId;
    private String inspectorName;
    private LocalDateTime inspectionDate;
    private String generalCondition;
    private List<InspectionDetailResponse> details;
    private String conclusion;
    private String recommendedAction;

    // Vehicle info
    private String vehicleVin;
    private String vehicleModel;
    private Integer mileage;
    private String customerName;
}