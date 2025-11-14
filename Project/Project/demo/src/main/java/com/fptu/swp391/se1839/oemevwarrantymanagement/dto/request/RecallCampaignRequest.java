package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecallCampaignRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Production date from is required")
    private LocalDate produceDateFrom;

    @NotNull(message = "Production date to is required")
    private LocalDate produceDateTo;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Risk level is required")
    private String riskLevel; // HIGH, MEDIUM, LOW

    @NotBlank(message = "Defect description is required")
    private String defectDescription;

    @NotBlank(message = "Remedy plan is required")
    private String remedyPlan;

    @NotNull(message = "Estimated parts needed is required")
    @Min(value = 1, message = "Estimated parts needed must be greater than 0")
    private Integer estimatedPartsNeeded;

    @NotNull(message = "Estimated cost per vehicle is required")
    @Min(value = 0, message = "Estimated cost per vehicle must not be negative")
    private Double estimatedCostPerVehicle;
}