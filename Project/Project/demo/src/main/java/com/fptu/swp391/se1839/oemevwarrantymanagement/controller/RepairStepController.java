package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChangeStatusRequest;
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
                        @PathVariable("id") Long repairStepId, @RequestBody ChangeStatusRequest status) {
                ChangeStatusRepairStepResponse changeStatusRepairStep = this.repairStepService
                                .changeStepStatus(repairStepId, status);
                var result = ApiResponse.<ChangeStatusRepairStepResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Change status step successfully")
                                .data(changeStatusRepairStep)
                                .build();
                return ResponseEntity.ok(result);
        }
}
