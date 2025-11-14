package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCenterResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ServiceCenterService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class ServiceCenterController {
    final ServiceCenterService serviceCenterService;
    @GetMapping("/service-centers")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<List<ServiceCenterResponse>>> getAllSC() {
        List<ServiceCenterResponse> serviceCenters = serviceCenterService.getAllSC();
        var result = ApiResponse.<List<ServiceCenterResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Get all SC successfully")
                .data(serviceCenters)
                .build();
        return ResponseEntity.ok(result);
    }
}
