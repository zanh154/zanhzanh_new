package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InspectionDetailRequest {
    @NotNull(message = "Part ID is required")
    private Long partId;

    @NotBlank(message = "Condition is required")
    private String condition; // GOOD, DAMAGED, NEEDS_REPLACEMENT

    private String notes;

    private String evidence; // URL to photos/videos
}