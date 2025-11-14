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

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.PartReceiptVoucherRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartReceiptVoucherResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartReceiptVoucherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/part-receipts")
@RequiredArgsConstructor
public class PartReceiptVoucherController {

    private final PartReceiptVoucherService voucherService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SC_STAFF')")
    public ResponseEntity<ApiResponse<PartReceiptVoucherResponse>> createVoucher(
            @Valid @RequestBody PartReceiptVoucherRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getClaim("userId").toString());

        PartReceiptVoucherResponse response = voucherService.createVoucher(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PartReceiptVoucherResponse>builder()
                        .status(HttpStatus.CREATED.toString())
                        .message("Part receipt voucher created successfully")
                        .data(response)
                        .build());
    }

    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF')")
    public ResponseEntity<ApiResponse<PartReceiptVoucherResponse>> updateStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        PartReceiptVoucherResponse response = voucherService.updateStatus(id, status);

        return ResponseEntity.ok(ApiResponse.<PartReceiptVoucherResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Status updated successfully")
                .data(response)
                .build());
    }

    @GetMapping("/service-center/{serviceCenterId}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PartReceiptVoucherResponse>>> getByServiceCenter(
            @PathVariable Long serviceCenterId) {
        List<PartReceiptVoucherResponse> responses = voucherService.getVouchersByServiceCenter(serviceCenterId);

        return ResponseEntity.ok(ApiResponse.<List<PartReceiptVoucherResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved vouchers successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/receiver/{receiverId}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PartReceiptVoucherResponse>>> getByReceiver(
            @PathVariable Long receiverId) {
        List<PartReceiptVoucherResponse> responses = voucherService.getVouchersByReceiver(receiverId);

        return ResponseEntity.ok(ApiResponse.<List<PartReceiptVoucherResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved vouchers successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<PartReceiptVoucherResponse>> getById(
            @PathVariable Long id) {
        PartReceiptVoucherResponse response = voucherService.getVoucherById(id);

        return ResponseEntity.ok(ApiResponse.<PartReceiptVoucherResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Retrieved voucher successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SC_STAFF')")
    public ResponseEntity<ApiResponse<Void>> deleteVoucher(
            @PathVariable Long id) {
        voucherService.deleteVoucher(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.toString())
                .message("Deleted voucher successfully")
                .build());
    }
}