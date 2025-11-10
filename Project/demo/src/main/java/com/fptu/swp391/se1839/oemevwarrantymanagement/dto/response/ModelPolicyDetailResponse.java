package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

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
public class ModelPolicyDetailResponse {
    Long partId;
    String partName;
    Long policyId;
    String policyName;
    String description;
    int durationPeriod;
    int mileageLimit;
    String startDate;
    String endDate;
}
