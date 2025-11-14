package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.AttachSerialRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ChangeStatusRepairStepResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.GetRepairStepResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairStepService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class RepairStepController {

        final RepairStepService repairStepService;
        final com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairOrderService repairOrderService;

        @GetMapping("/repair-steps/{id}")
        public ResponseEntity<ApiResponse<List<GetRepairStepResponse>>> getStep(
                        @PathVariable("id") Long repairOrderId) {
                List<GetRepairStepResponse> response = this.repairStepService.handleGetRepairStep(repairOrderId);
                var result = ApiResponse.<List<GetRepairStepResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get Step successfully")
                                .data(response)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PatchMapping("/repair-steps/{id}")
        public ResponseEntity<ApiResponse<ChangeStatusRepairStepResponse>> changeStatus(
                        @PathVariable("id") Long repairStepId) {
                ChangeStatusRepairStepResponse changeStatusRepairStep = this.repairStepService
                                .completeRepairStep(repairStepId);
                var result = ApiResponse.<ChangeStatusRepairStepResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Change status step successfully")
                                .data(changeStatusRepairStep)
                                .build();
                return ResponseEntity.ok(result);
        }

        @PostMapping("/repair-steps/{orderId}/start")
        public ResponseEntity<ApiResponse<String>> startRepairOrder(@PathVariable Long orderId) {
                try {
                        repairOrderService.startRepairOrder(orderId);
                        var resp = ApiResponse.<String>builder()
                                        .status(HttpStatus.OK.toString())
                                        .message("Repair order started successfully")
                                        .data("✅ Repair order started successfully!")
                                        .build();
                        return ResponseEntity.ok(resp);
                } catch (Exception e) {
                        var resp = ApiResponse.<String>builder()
                                        .status(HttpStatus.BAD_REQUEST.toString())
                                        .message("Failed to start repair order: " + e.getMessage())
                                        .data(null)
                                        .build();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
                }
        }

        @PutMapping("/{repairOrderId}/assembly")
        public ResponseEntity<ApiResponse<String>> attachNewSerialNumbers(
                        @PathVariable long repairOrderId,
                        @RequestBody AttachSerialRequest request) {

                repairStepService.attachNewSerialNumbers(repairOrderId, request);
                return ResponseEntity.ok(
                                ApiResponse.<String>builder()
                                                .status("200 OK")
                                                .message("Serial numbers attached successfully")
                                                .build());
        }
}
