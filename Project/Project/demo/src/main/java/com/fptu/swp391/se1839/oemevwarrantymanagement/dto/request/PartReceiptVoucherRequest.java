package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PartReceiptVoucherRequest {
    @NotNull(message = "Service center ID is required")
    private Long serviceCenterId;

    private String notes;

    @NotEmpty(message = "Details must not be empty")
    @Valid
    private List<PartReceiptDetailRequest> details;
}