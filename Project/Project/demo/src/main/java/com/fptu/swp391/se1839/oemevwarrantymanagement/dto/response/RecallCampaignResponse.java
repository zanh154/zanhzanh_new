package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecallCampaignResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate produceDateFrom;
    private LocalDate produceDateTo;
    private String code;
    private String riskLevel;
    private String defectDescription;
    private String remedyPlan;
    private Integer estimatedPartsNeeded;
    private Double estimatedCostPerVehicle;

    // Thống kê
    private Integer totalVehicles;
    private Map<String, Integer> progressSummary; // Map của status -> count
    private Double completionRate;
    private Double estimatedTotalCost;
}