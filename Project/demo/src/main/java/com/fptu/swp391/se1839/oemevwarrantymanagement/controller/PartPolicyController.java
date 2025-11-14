package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePartPolicyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPartPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyCodeResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PartWarrantyInfoResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartPolicyService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PolicyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartPolicyController {

        final PartPolicyService partPolicyService;
        final PolicyService policyService;

        @GetMapping("/part-policies")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<GetAllPartPolicyResponse>> getAllPartPolicies(
                        @AuthenticationPrincipal Jwt jwt) {

                GetAllPartPolicyResponse policies = partPolicyService.handleGetAllPartPolicies();
                var result = ApiResponse.<GetAllPartPolicyResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get all part policies successfully")
                                .data(policies)
                                .build();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/part-policies/{id}")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<PartPolicyDetailResponse>> getPartPolicyDetail(
                        @PathVariable Long id,
                        @AuthenticationPrincipal Jwt jwt) {

                PartPolicyDetailResponse detail = partPolicyService.handleGetPartPolicyById(id);

                var result = ApiResponse.<PartPolicyDetailResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get part policy detail successfully")
                                .data(detail)
                                .build();

                return ResponseEntity.ok(result);
        }

        @PostMapping("/part-policy")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<PartPolicyResponse>> createPartPolicy(
                        @Validated @RequestBody CreatePartPolicyRequest request,
                        @AuthenticationPrincipal Jwt jwt) {

                PartPolicyResponse response = partPolicyService.handleCreatePartPolicy(request);
                var result = ApiResponse.<PartPolicyResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message("Part policy created successfully")
                                .data(response)
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }

        @GetMapping("/part-policy/code")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<PartPolicyCodeResponse>> getPartPolicyCode(
                        @Validated @AuthenticationPrincipal Jwt jwt) {

                PartPolicyCodeResponse response = partPolicyService.handleGetPartPolicyCode();
                var result = ApiResponse.<PartPolicyCodeResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message("Get part policy code successfully")
                                .data(response)
                                .build();

                return ResponseEntity.status(HttpStatus.OK).body(result);
        }

        @PutMapping("/part-policy/status/{partPolicyId}")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<PartPolicyResponse>> updatePartPolicy(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long partPolicyId) {

                PartPolicyResponse response = partPolicyService.handleUpdateStatusPartPolicy(partPolicyId);

                var result = ApiResponse.<PartPolicyResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Part policy updated successfully")
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }

        @GetMapping("/part-policy/check/{serialNumber}")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF','SC_STAFF')")
        public ResponseEntity<ApiResponse<PartWarrantyInfoResponse>> checkPartWarranty(
                @PathVariable String serialNumber) {

        PartWarrantyInfoResponse response = partPolicyService.getWarrantyInfoBySerial(serialNumber);

        var result = ApiResponse.<PartWarrantyInfoResponse>builder()
                .status(HttpStatus.OK.toString())
                .message("Warranty info retrieved successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(result);
        }
}