package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApproveOrRejectPartRequest {
    private Long partSupplyId;
    private String action; // APPROVE hoặc REJECT
    private String note; // ghi chú chung cho yêu cầu
    private List<PartApprovalDetailResquest> details;
}