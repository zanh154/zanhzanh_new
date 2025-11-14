package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartSupplyDetailResponse {
    Long id;
    String serviceCenterName;
    String createdBy;
    LocalDateTime createdDate;
    String status;
    String note;
    List<PartRequestDetailResponse> details;
}