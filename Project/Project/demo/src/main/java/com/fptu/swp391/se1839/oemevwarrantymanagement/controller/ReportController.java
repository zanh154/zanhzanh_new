package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimsByComponentResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ClaimsByPriorityResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CostAnalysisResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.FailureAnalysisResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ModelFailureResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairOrderDurationStatusResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.SCExpenseResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCenterPerformanceResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.TechnicianPerformanceResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.claimsByCategoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartClaimService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairOrderService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ReportService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.SCExpenseService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.UserService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.WarrantyClaimService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class ReportController {
        final WarrantyClaimService warrantyClaimService;
        final RepairOrderService repairOrderService;
        final SCExpenseService scExpenseService;
        final UserService userService;
        final PartClaimService partClaimService;
        final ReportService reportService;

        @GetMapping("/report/summary")
        public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(@AuthenticationPrincipal Jwt jwt) {

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;
                // Tính toán summary
                OrderSummaryResponse orderSummary = repairOrderService
                                .handleCalculateResolutionRateDifferent(serviceCenterId);
                ClaimSummaryResponse claimSummary = warrantyClaimService
                                .calculateRepeatClaimsRateWithComparison(serviceCenterId);
                SCExpenseResponse expense = scExpenseService.handleWarrantyCostComparison(serviceCenterId);
                TechnicianPerformanceResponse technicianPerformance = userService
                                .calculateTechnicianPerformanceWithComparison(serviceCenterId);

                Map<String, Object> summaryMap = Map.of(
                                "orderSummary", orderSummary,
                                "claimSummary", claimSummary,
                                "technicianPerformanceSummary", technicianPerformance,
                                "expenseSummary", expense);

                ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Summary fetched successfully")
                                .data(summaryMap)
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/report/failure-analysis")
        public ResponseEntity<ApiResponse<FailureAnalysisResponse>> getFailureAnalysis(
                        @AuthenticationPrincipal Jwt jwt) {

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                List<ModelFailureResponse> modelFailure = warrantyClaimService
                                .calculateFailureAnalysisByModel(serviceCenterId);
                List<ClaimsByPriorityResponse> claimsByPriority = warrantyClaimService
                                .calculateClaimsByPriority(serviceCenterId);
                List<claimsByCategoryResponse> claimsByCategory = partClaimService
                                .calculateClaimsByCategory(serviceCenterId);
                List<ClaimsByComponentResponse> claimsByComponent = partClaimService
                                .calculateClaimsByComponent(serviceCenterId);

                FailureAnalysisResponse failureAnalysis = FailureAnalysisResponse.builder()
                                .claimsByCategory(claimsByCategory)
                                .claimsByPriority(claimsByPriority)
                                .failureRateByVehicleModel(modelFailure)
                                .failuresByComponent(claimsByComponent)
                                .build();

                ApiResponse<FailureAnalysisResponse> response = ApiResponse.<FailureAnalysisResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Failure Analysis fetched successfully")
                                .data(failureAnalysis)
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/report/cost-analysis")
        public ResponseEntity<ApiResponse<CostAnalysisResponse>> getCostAnalysis(@AuthenticationPrincipal Jwt jwt) {

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                CostAnalysisResponse costAnalysis = warrantyClaimService
                                .handleCalculateClaimCostByMonth(serviceCenterId);

                ApiResponse<CostAnalysisResponse> response = ApiResponse.<CostAnalysisResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Failure Analysis fetched successfully")
                                .data(costAnalysis)
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/report/performances")
        @PreAuthorize("hasAnyAuthority('ADMIN', 'EVM_STAFF')")
        public ResponseEntity<ApiResponse<List<ServiceCenterPerformanceResponse>>> getServiceCenterPerformance(
                        @AuthenticationPrincipal Jwt jwt) {

                List<ServiceCenterPerformanceResponse> data = reportService.getServiceCenterPerformance();

                ApiResponse<List<ServiceCenterPerformanceResponse>> response = ApiResponse
                                .<List<ServiceCenterPerformanceResponse>>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Get service center performance successfully at " + LocalDateTime.now())
                                .data(data)
                                .build();

                return ResponseEntity.ok(response);
        }

        @GetMapping("/report/completed-duration")
        @PreAuthorize("hasAnyAuthority('ADMIN','EVM_STAFF')")
        public ResponseEntity<ApiResponse<RepairOrderDurationStatusResponse>> getCompletedOrderDurationStats() {

                RepairOrderDurationStatusResponse data = reportService.getCompletedOrderDurationStats();

                var response = ApiResponse.<RepairOrderDurationStatusResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Completed repair orders duration stats at " + LocalDateTime.now())
                                .data(data)
                                .build();

                return ResponseEntity.ok(response);
        }
}
