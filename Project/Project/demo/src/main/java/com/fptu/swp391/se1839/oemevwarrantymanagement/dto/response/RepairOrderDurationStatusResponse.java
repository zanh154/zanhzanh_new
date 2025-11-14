package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RepairOrderDurationStatusResponse {
    long under24h;
    long under72h;
    long under168h;
    long over168h;
}