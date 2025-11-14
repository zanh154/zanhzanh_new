package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreateClaimRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.FilterRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.WarrantyClaimStatusRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimDashboardResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CreateClaimResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.WarrantyClaimStatusResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.WarrantyClaimService;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarrantyClaimController {

        final WarrantyClaimService warrantyClaimService;

        @PostMapping(value = "/claims", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasAnyAuthority('ADMIN','TECHNICIAN')")
        public ResponseEntity<ApiResponse<CreateClaimResponse>> createClaim(
                        @RequestPart("claim") CreateClaimRequest request,
                        @RequestPart(value = "attachments", required = false) MultipartFile[] attachments,
                        @AuthenticationPrincipal Jwt jwt) throws IOException {

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                Long userId = Long.parseLong(jwt.getClaim("userId").toString());
                CreateClaimResponse response = warrantyClaimService.handleCreateClaim(request, serviceCenterId,
                                attachments, userId);

                var result = ApiResponse.<CreateClaimResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message("Create claim successfully")
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/claims")
        public ResponseEntity<ApiResponse<ClaimDashboardResponse>> claimSummary(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestBody(required = false) FilterRequest request) {

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                if (request == null) {
                        request = new FilterRequest();
                }

                Long userId = Long.parseLong(jwt.getClaim("userId").toString());

                ClaimDashboardResponse cdr = this.warrantyClaimService.handleClaimDashboard(serviceCenterId, request,
                                userId);

                var result = ApiResponse.<ClaimDashboardResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get claim summary successfully")
                                .data(cdr)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PatchMapping("/claims/{id}")
        public ResponseEntity<ApiResponse<WarrantyClaimStatusResponse>> changeStatus(
                        @PathVariable("id") long claimId,
                        @RequestBody WarrantyClaimStatusRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                Long userId = Long.parseLong(jwt.getClaim("userId").toString());
                WarrantyClaimStatusResponse warrantyClaimAfter = this.warrantyClaimService.handleChangeStatus(claimId,
                                request,
                                userId);
                var result = ApiResponse.<WarrantyClaimStatusResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Change status claim successfully")
                                .data(warrantyClaimAfter)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/claims/{id}")
        public ResponseEntity<ApiResponse<ClaimDetailResponse>> getClaimDetail(
                        @PathVariable("id") long claimId,
                        @AuthenticationPrincipal Jwt jwt) throws Exception {
                Long userId = Long.parseLong(jwt.getClaim("userId").toString());
                ClaimDetailResponse detail = this.warrantyClaimService.handleGetClaimDetail(claimId, userId);
                var result = ApiResponse.<ClaimDetailResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get claim detail successfully")
                                .data(detail)
                                .build();
                return ResponseEntity.ok(result);
        }

}
