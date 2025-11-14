package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.Builder;
import lombok.Value;

@Builder
@Value

public class ServiceCenterPerformanceResponse {
    Long scId;
    String scName;
    Long totalCompletedOrder;
}