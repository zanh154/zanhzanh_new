package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ApproveOrRejectPartRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePartSupplyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CreatePartSupplyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartSupplyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartSupplyDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartSupplyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartSupplyController {

        final PartSupplyService partSupplyService;

        @PostMapping("/part-supply")
        @PreAuthorize("hasAuthority('SC_STAFF')")
        public ResponseEntity<ApiResponse<CreatePartSupplyResponse>> createPartSupply(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestBody CreatePartSupplyRequest request) {

                Long userId = Long.valueOf(jwt.getClaim("userId").toString());

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                CreatePartSupplyResponse response = partSupplyService.handleCreatePartSupply(request, userId,
                                serviceCenterId);

                var result = ApiResponse.<CreatePartSupplyResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message(response.getMessage())
                                .data(response)
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }

        @GetMapping("/part-supplies")
        @PreAuthorize("hasAnyAuthority('EVM_STAFF', 'ADMIN')")
        public ResponseEntity<ApiResponse<GetAllPartSupplyResponse>> getAllPartSupplies(
                        @AuthenticationPrincipal Jwt jwt) {

                GetAllPartSupplyResponse response = partSupplyService.handleGetAllPartSupplies();

                ApiResponse<GetAllPartSupplyResponse> result = ApiResponse.<GetAllPartSupplyResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get all part supplies successfully")
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/part-supply/{id}")
        @PreAuthorize("hasAnyAuthority('EVM_STAFF', 'ADMIN', 'SC_STAFF')")
        public ResponseEntity<ApiResponse<PartSupplyDetailResponse>> getPartSupplyDetail(
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt) {

                PartSupplyDetailResponse response = partSupplyService.handleGetPartSupplyDetail(id);

                ApiResponse<PartSupplyDetailResponse> result = ApiResponse.<PartSupplyDetailResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get part supply detail successfully")
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }

        @PutMapping("/part-supply/review")
        @PreAuthorize("hasAnyAuthority('EVM_STAFF', 'ADMIN')")
        public ResponseEntity<ApiResponse<PartSupplyDetailResponse>> reviewPartSupply(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestBody ApproveOrRejectPartRequest request) {

                Long staffId = Long.valueOf(jwt.getClaim("userId").toString());

                PartSupplyDetailResponse response = partSupplyService.handleReviewPartSupply(request, staffId);

                ApiResponse<PartSupplyDetailResponse> result = ApiResponse.<PartSupplyDetailResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Part supply request reviewed successfully")
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }
}