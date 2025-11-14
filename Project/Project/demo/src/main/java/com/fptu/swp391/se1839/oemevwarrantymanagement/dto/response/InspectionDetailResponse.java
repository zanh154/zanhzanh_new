package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionDetailResponse {
    private Long id;
    private Long partId;
    private String partName;
    private String partNumber;
    private String condition;
    private String notes;
    private String evidence;
}