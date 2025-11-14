package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartReceiptVoucherResponse {
    private Long id;
    private Long serviceCenterId;
    private String serviceCenterName;
    private Long receiverId;
    private String receiverName;
    private LocalDateTime receiveDate;
    private String notes;
    private String status;
    private List<PartReceiptDetailResponse> details;
}