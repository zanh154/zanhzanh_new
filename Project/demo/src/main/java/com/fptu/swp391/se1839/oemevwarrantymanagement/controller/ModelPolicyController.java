package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ModelPolicyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModelPolicyController {

    final ModelPolicyService modelPolicyService;

    @GetMapping("/vehicles/policy/{vin}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF', 'EVM_STAFF', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ModelPolicyResponse>> getPolicyByVehicleVin(@PathVariable String vin) {

        ModelPolicyResponse data = modelPolicyService.getWarrantyPolicyByVin(vin);

        var result = ApiResponse.<ModelPolicyResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Get warranty policy by VIN successfully")
                .data(data)
                .build();

        return ResponseEntity.ok(result);
    }
}
