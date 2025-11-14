package com.fptu.swp391.se1839.oemevwarrantymanagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DashboardResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.MonthlyCostSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RecentActivityResponse;

import static com.fptu.swp391.se1839.oemevwarrantymanagement.Utilities.TimeUtil.formatTimeAgo;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ApiResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.CostAnalysisResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DashboardClaimSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DashboardOrderSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ActivityLogService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.PartInventoryService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.RepairOrderService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.SCExpenseService;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.WarrantyClaimService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
public class DashboardController {
        final RepairOrderService repairOrderService;
        final WarrantyClaimService warrantyClaimService;
        final ActivityLogService activityLogService;
        final PartInventoryService partInventoryService;
        final SCExpenseService scExpenseService;

        @GetMapping("/summary")
        public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardSummary(@AuthenticationPrincipal Jwt jwt) {

                Object scClaim = jwt.getClaim("serviceCenterId");
                Long serviceCenterId = (scClaim != null) ? Long.parseLong(scClaim.toString()) : 0L;

                DashboardOrderSummaryResponse orderSummary = repairOrderService.findSunSummaryOrder(serviceCenterId);
                DashboardClaimSummaryResponse claimSummary = warrantyClaimService.handleSummaryClaims(serviceCenterId);
                java.util.Map<String, Long> claimBreakdown = warrantyClaimService
                                .getClaimCountsBreakdown(serviceCenterId);

                // Reuse existing cost analysis for monthly summaries and total warranty cost
                CostAnalysisResponse costAnalysis = warrantyClaimService
                                .handleCalculateClaimCostByMonth(serviceCenterId);
                List<MonthlyCostSummaryResponse> monthlySummaries = costAnalysis
                                .getMonthlySummaries();
                long totalWarrantyCost = costAnalysis.getTotalWarrantyCost();

                List<RecentActivityResponse> recentActivities = activityLogService.findRecentActivities()
                                .stream()
                                .map(a -> new RecentActivityResponse(
                                                a.getTitle(),
                                                a.getDescription(),
                                                formatTimeAgo(a.getCreatedAt()),
                                                a.getStatus()))
                                .toList();

                Map<String, Object> data = new HashMap<>();
                data.put("orders", orderSummary);
                data.put("claims", claimSummary);
                data.put("claimsBreakdown", claimBreakdown);
                data.put("monthlyTrend", monthlySummaries);
                data.put("totalWarrantyCost", totalWarrantyCost);

                int resolutionRate = this.repairOrderService.handleCalculateResolutionRate(serviceCenterId);
                int onTimePerformance = this.repairOrderService.handleCalculatePerformanceMetrics(serviceCenterId);
                int partAvailability = this.partInventoryService.handleCalculatePartAvailability(serviceCenterId);
                int responseTimeScore = this.repairOrderService.handleCalculateResponseScore(serviceCenterId);

                Map<String, Integer> performanceMetrics = new HashMap<>();
                performanceMetrics.put("Resolution Rate", resolutionRate);
                performanceMetrics.put("On-Time Performance", onTimePerformance);
                performanceMetrics.put("Part Availability", partAvailability);
                performanceMetrics.put("Response Time Score", responseTimeScore);

                data.put("performanceMetrics", performanceMetrics);

                int overdueRepairs = this.repairOrderService.handleCalculateOverdueRepairs(serviceCenterId);
                int lowStockWarnings = this.partInventoryService.countLowStockParts(serviceCenterId);

                Map<String, Integer> urgentItems = new HashMap<>();
                urgentItems.put("High Priority Claims", claimSummary.getEmegency());
                urgentItems.put("Overdue Repairs", overdueRepairs);
                urgentItems.put("Low Stock Warnings", lowStockWarnings);

                data.put("urgentItems", urgentItems);

                int completedToday = this.repairOrderService.hanldeCalculateCompleteToday(serviceCenterId);
                double revenueThisWeek = this.scExpenseService.handleCalculateRevenueOfWeek(serviceCenterId);
                double avgRepairDays = this.repairOrderService.handleCalculateAvgDays(serviceCenterId);

                Map<String, Object> quickStatistics = new HashMap<>();
                quickStatistics.put("Completed Today", completedToday);
                quickStatistics.put("Revenue This Week", revenueThisWeek);
                quickStatistics.put("Average Repair Days", avgRepairDays);

                data.put("quickStatistics", quickStatistics);

                DashboardResponse dashboardResponse = DashboardResponse.builder()
                                .dashboardMap(data)
                                .recentActiveList(recentActivities)
                                .performanceMetrics(performanceMetrics)
                                .quickStatistics(quickStatistics)
                                .urgentItems(urgentItems)
                                .build();

                ApiResponse<DashboardResponse> result = ApiResponse.<DashboardResponse>builder()
                                .status(HttpStatus.OK.toString())
                                .message("Dashboard summary fetched successfully")
                                .data(dashboardResponse)
                                .build();

                return ResponseEntity.ok(result);
        }
}
