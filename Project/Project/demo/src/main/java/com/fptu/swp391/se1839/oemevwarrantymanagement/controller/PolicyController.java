package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.CreatePolicyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.UpdatePolicyRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CreatePolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DeletePolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllPolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.PolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.UpdatePolicyResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PolicyService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PolicyController {

        private final PolicyService policyService;

        @GetMapping("/policies")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<GetAllPolicyResponse>> getAllPolicies(@AuthenticationPrincipal Jwt jwt) {
                GetAllPolicyResponse policies = policyService.handleGetAllPolicy();
                var result = ApiResponse.<GetAllPolicyResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get all policies successfully")
                                .data(policies)
                                .build();
                return ResponseEntity.ok(result);
        }

        @GetMapping("/policy/{policyId}")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<PolicyResponse>> getPolicyById(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long policyId) {
                PolicyResponse policy = policyService.handleGetPolicyById(policyId);
                var result = ApiResponse.<PolicyResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get policy by ID successfully")
                                .data(policy)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PostMapping("/policy")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<CreatePolicyResponse>> createPolicy(
                        @Validated @RequestBody CreatePolicyRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                CreatePolicyResponse response = policyService.handleCreatePolicy(request);
                var result = ApiResponse.<CreatePolicyResponse>builder()
                                .status(HttpStatus.CREATED.toString())
                                .message("Policy created successfully")
                                .data(response)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }

        @PutMapping("/policy/{policyId}")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<UpdatePolicyResponse>> updatePolicy(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long policyId,
                        @Valid @RequestBody UpdatePolicyRequest request) {

                UpdatePolicyResponse response = policyService.handleUpdatePolicy(policyId, request);

                var result = ApiResponse.<UpdatePolicyResponse>builder()
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }

        @DeleteMapping("/policy/{policyId}")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<DeletePolicyResponse>> deletePolicy(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long policyId) {

                DeletePolicyResponse response = policyService.handleDeletePolicy(policyId);

                var result = ApiResponse.<DeletePolicyResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message(response.getMessage())
                                .data(response)
                                .build();

                return ResponseEntity.ok(result);
        }
	
	@PatchMapping("/policy/status/{policyId}")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ApiResponse<String>> updateStatusPolicy(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long policyId) {

                String response = policyService.updatePolicyStatus(policyId);

                var result = ApiResponse.<String>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Policy status changed successfully")
                                .data(response)
                                .build();
                return ResponseEntity.ok(result);
        }
}