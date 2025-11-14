package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCampaignResponse {
    private Long id;
    String name;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate produceDateFrom;
    LocalDate produceDateTo;
    String code;
    int totalVehicles;
}
