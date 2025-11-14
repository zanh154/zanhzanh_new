package com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RecallVehiclesRequest {
    @NotEmpty(message = "Vehicle VINs must not be empty")
    private List<String> vehicleVins;
}