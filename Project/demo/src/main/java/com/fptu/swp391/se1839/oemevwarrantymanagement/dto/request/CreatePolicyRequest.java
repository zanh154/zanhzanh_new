package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePolicyRequest {
    String code;
    String name;

    int durationPeriod;

    int mileageLimit;
    String description;

    PolicyType type;

    public enum PolicyType {
        NORMAL,
        PROMOTION
    }
}