package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartWarrantyInfoResponse {
    private String policyName;
    private String startDate;
    private String endDate;
    private Integer mileageLimit;
}

