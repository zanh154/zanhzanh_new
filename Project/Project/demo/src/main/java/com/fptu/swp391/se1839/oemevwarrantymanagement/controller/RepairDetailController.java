package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetAllRepairDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairDetailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class RepairDetailController {
        final RepairDetailService repairDetailService;

        @GetMapping("/repair-details/{id}")
        public ResponseEntity<ApiResponse<GetAllRepairDetailResponse>> getRepairDetail(
                        @PathVariable("id") Long repairOrderId,
                        @AuthenticationPrincipal Jwt jwt) {
                GetAllRepairDetailResponse gadr = this.repairDetailService.handleGetRepairDetail(repairOrderId);
                var result = ApiResponse.<GetAllRepairDetailResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get Repair Order successfully")
                                .data(gadr)
                                .build();
                return ResponseEntity.ok(result);
        }
}
