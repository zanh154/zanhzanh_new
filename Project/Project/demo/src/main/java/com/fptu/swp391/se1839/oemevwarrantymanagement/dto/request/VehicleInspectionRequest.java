package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleInspectionRequest {
    @NotNull(message = "Warranty claim ID is required")
    private Long claimId;

    @NotBlank(message = "General condition is required")
    private String generalCondition;

    @NotEmpty(message = "Inspection details must not be empty")
    @Valid
    private List<InspectionDetailRequest> details;

    @NotBlank(message = "Conclusion is required")
    private String conclusion;

    @NotBlank(message = "Recommended action is required")
    private String recommendedAction;
}