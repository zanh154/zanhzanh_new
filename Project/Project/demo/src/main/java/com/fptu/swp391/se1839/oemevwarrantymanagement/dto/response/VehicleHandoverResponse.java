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
public class VehicleHandoverResponse {
    private Long id;
    private Long repairOrderId;
    private String repairOrderStatus;
    private Long handoverById;
    private String handoverByName;
    private LocalDateTime handoverDate;
    private String checklistDetails;
    private String customerSignature;
    private String staffSignature;
    private String status;
    private String rejectReason;

    // Vehicle info
    private String vehicleVin;
    private String vehicleModel;
    private String customerName;
    private String customerPhone;
}