package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.time.LocalDate;
import java.util.List;

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
public class CreateCampaignRequest {
    String name;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    LocalDate produceDateFrom;
    LocalDate produceDateTo;
    List<Long> affectedModelIds;
    String code;
}
