package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartReceiptDetailResponse {
    private Long id;
    private String partId;
    private String partName;
    private Integer quantity;
    private String condition;
    private String notes;
}