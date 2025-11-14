package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRepairDetailRequest {
    private Long repairOrderId;
    private Long partId;
    private String vehiclePartId;
}