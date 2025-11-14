package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PartReceiptDetailRequest {
    @NotNull(message = "Part ID is required")
    private String partId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;

    @NotBlank(message = "Condition is required")
    private String condition;

    private String notes;
}