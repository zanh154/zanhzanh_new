package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AllEvidenceRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.AllEvidenceResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairOrderEvidenceService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepairOrderEvidenceController {

    final RepairOrderEvidenceService repairOrderEvidenceService;

    // Lưu file trên Docker, trả Base64 (chỉ dùng cho front-end hiển thị)
    @PostMapping("/{id}/evidence")
    public ResponseEntity<ApiResponse<String>> uploadEvidence(
            @PathVariable("id") Long repairOrderId,
            @RequestBody AllEvidenceRequest request,
            @AuthenticationPrincipal Jwt jwt) throws IOException {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());

        repairOrderEvidenceService.handleCreateEvidence(repairOrderId, request, userId);
        var result = ApiResponse.<String>builder()
                .status(HttpStatus.OK.toString())
                .message("Create Evidnece successfully")
                .build();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/evidence")
    public ResponseEntity<ApiResponse<List<AllEvidenceResponse>>> getAllEvidence(
            @PathVariable("id") Long repairOrderId) throws IOException {

        List<AllEvidenceResponse> evidenceList = repairOrderEvidenceService.getEvidenceByRepairOrderId(repairOrderId);

        var response = ApiResponse.<List<AllEvidenceResponse>>builder()
                .status(HttpStatus.OK.toString())
                .message("Fetched all evidence successfully")
                .data(evidenceList)
                .build();

        return ResponseEntity.ok(response);
    }

}
