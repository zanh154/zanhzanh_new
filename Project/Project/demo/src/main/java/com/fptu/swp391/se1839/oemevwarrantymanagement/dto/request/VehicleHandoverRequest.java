package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleHandoverRequest {
    @NotNull(message = "Repair order ID is required")
    private Long repairOrderId;

    @NotBlank(message = "Checklist details is required")
    private String checklistDetails; // JSON của checklist kiểm tra

    @NotBlank(message = "Customer signature is required")
    private String customerSignature;

    @NotBlank(message = "Staff signature is required")
    private String staffSignature;
}