package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

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
public class PartRequestDetailResponse {
    Long id;
    String partCode;
    String partName;
    int requestedQuantity;
    Integer approvedQuantity;
    String remark;
}