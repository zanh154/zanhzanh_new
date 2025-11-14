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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.VehicleHandoverRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.VehicleHandoverResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.VehicleHandoverService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehicle-handovers")
@RequiredArgsConstructor
public class VehicleHandoverController {

    private final VehicleHandoverService handoverService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SC_STAFF')")
    public ResponseEntity<ApiResponse<VehicleHandoverResponse>> createHandover(
            @Valid @RequestBody VehicleHandoverRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());

        VehicleHandoverResponse response = handoverService.createHandover(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<VehicleHandoverResponse>builder()
                        .status(HttpStatus.CREATED.toString())
                        .message("Vehicle handover created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('SC_STAFF')")
    public ResponseEntity<ApiResponse<VehicleHandoverResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String rejectReason) {
        VehicleHandoverResponse response = handoverService.updateStatus(id, status, rejectReason);

        return ResponseEntity.ok(ApiResponse.<VehicleHandoverResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Status updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/service-center/{serviceCenterId}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VehicleHandoverResponse>>> getByServiceCenter(
            @PathVariable Long serviceCenterId) {
        List<VehicleHandoverResponse> responses = handoverService.getHandoversByServiceCenter(serviceCenterId);

        return ResponseEntity.ok(ApiResponse.<List<VehicleHandoverResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved handovers successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VehicleHandoverResponse>>> getByStaff(
            @PathVariable Long staffId) {
        List<VehicleHandoverResponse> responses = handoverService.getHandoversByStaff(staffId);

        return ResponseEntity.ok(ApiResponse.<List<VehicleHandoverResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved handovers successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<VehicleHandoverResponse>> getById(
            @PathVariable Long id) {
        VehicleHandoverResponse response = handoverService.getHandoverById(id);

        return ResponseEntity.ok(ApiResponse.<VehicleHandoverResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved handover successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF')")
    public ResponseEntity<ApiResponse<Void>> deleteHandover(
            @PathVariable Long id) {
        handoverService.deleteHandover(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Deleted handover successfully")
                .build());
    }
}