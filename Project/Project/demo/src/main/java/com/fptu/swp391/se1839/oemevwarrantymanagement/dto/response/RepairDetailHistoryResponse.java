package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepairDetailHistoryResponse {
    private String partName;
    private String status;
    private String description;
}