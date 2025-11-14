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
public class PolicyResponse {
    Long id;
    String code;
    String name;
    int durationPeriod;
    int mileageLimit;
    String description;
    PolicyType policyType;

    public enum PolicyType {
        NORMAL,
        PROMOTION
    }

    Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

}