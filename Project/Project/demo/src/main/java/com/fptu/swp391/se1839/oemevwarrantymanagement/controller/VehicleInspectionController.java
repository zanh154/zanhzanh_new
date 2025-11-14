package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleInspectionRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleInspectionResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.VehicleInspectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehicle-inspections")
@RequiredArgsConstructor
public class VehicleInspectionController {

    private final VehicleInspectionService inspectionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN')")
    public ResponseEntity<ApiResponse<VehicleInspectionResponse>> createInspection(
            @Valid @RequestBody VehicleInspectionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());

        VehicleInspectionResponse response = inspectionService.createInspection(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<VehicleInspectionResponse>builder()
                        .status(HttpStatus.CREATED.toString())
                        .message("Vehicle inspection created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN')")
    public ResponseEntity<ApiResponse<VehicleInspectionResponse>> updateInspection(
            @PathVariable Long id,
            @Valid @RequestBody VehicleInspectionRequest request) {
        VehicleInspectionResponse response = inspectionService.updateInspection(id, request);

        return ResponseEntity.ok(ApiResponse.<VehicleInspectionResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Inspection updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/service-center/{serviceCenterId}")
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN', 'SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VehicleInspectionResponse>>> getByServiceCenter(
            @PathVariable Long serviceCenterId) {
        List<VehicleInspectionResponse> responses = inspectionService.getInspectionsByServiceCenter(serviceCenterId);

        return ResponseEntity.ok(ApiResponse.<List<VehicleInspectionResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved inspections successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/inspector/{inspectorId}")
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN', 'SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VehicleInspectionResponse>>> getByInspector(
            @PathVariable Long inspectorId) {
        List<VehicleInspectionResponse> responses = inspectionService.getInspectionsByInspector(inspectorId);

        return ResponseEntity.ok(ApiResponse.<List<VehicleInspectionResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved inspections successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/claim/{claimId}")
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN', 'SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<VehicleInspectionResponse>> getByClaimId(
            @PathVariable Long claimId) {
        VehicleInspectionResponse response = inspectionService.getInspectionByClaimId(claimId);

        return ResponseEntity.ok(ApiResponse.<VehicleInspectionResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved inspection successfully")
                .data(response)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN', 'SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<VehicleInspectionResponse>> getById(
            @PathVariable Long id) {
        VehicleInspectionResponse response = inspectionService.getInspectionById(id);

        return ResponseEntity.ok(ApiResponse.<VehicleInspectionResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved inspection successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteInspection(
            @PathVariable Long id) {
        inspectionService.deleteInspection(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Deleted inspection successfully")
                .build());
    }
}