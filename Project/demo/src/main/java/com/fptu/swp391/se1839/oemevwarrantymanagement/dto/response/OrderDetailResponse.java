package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    FilterOrderResponse filterOrderResponse;
    GetTechnicalsResponse getTechnicalsResponse;
    Long repairOrderId;
    String signature;
    String notes;
    List<DecodeImageReponse> attachmentPaths;
    boolean acceptedResponsibility;
    LocalDateTime verifiedAt;
    String verifiedBy;
}
