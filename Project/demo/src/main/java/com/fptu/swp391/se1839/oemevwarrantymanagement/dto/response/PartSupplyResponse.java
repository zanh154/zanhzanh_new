package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;

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
public class PartSupplyResponse {
    Long id;
    String serviceCenterName;
    String createdBy;
    LocalDateTime createdDate;
    String status;
    String note;
}