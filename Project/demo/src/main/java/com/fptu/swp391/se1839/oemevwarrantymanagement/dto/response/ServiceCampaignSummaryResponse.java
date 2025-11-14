package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCampaignSummaryResponse {
    private Long campaignId;
    private String campaignCode;
    private String campaignName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate produceDateFrom;
    private LocalDate produceDateTo;
    private String status; // optional
}

