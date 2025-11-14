package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.ChooseTechnicalRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.FilterRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.request.RepairOrderVerificationRequest;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ChooseTechnicalResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.DashboardOrderSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderDashboardResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderDetailResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.OrderSummaryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairHistoryResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairOrderVerificationResponse;

public interface RepairOrderService {
        DashboardOrderSummaryResponse findSunSummaryOrder(Long serviceCenterId);

        OrderDashboardResponse handleOrderDashboard(long serviceCenterId,
                        FilterRequest request, Long userId);

        ChooseTechnicalResponse handleChooseTechnical(long repairOrderId, ChooseTechnicalRequest request);

        OrderDetailResponse handleGetDetailOrder(long serviceCenterId, long orderId) throws Exception;

        int handleCalculateResponseScore(Long serviceCenterId);

        int handleCalculatePerformanceMetrics(Long serviceCenterId);

        int handleCalculateOverdueRepairs(Long serviceCenterId);

        int hanldeCalculateCompleteToday(long serviceCenterId);

        double handleCalculateAvgDays(long serviceCenterId);

        int handleCalculateResolutionRate(Long serviceCenterId);

        OrderSummaryResponse handleCalculateResolutionRateDifferent(Long serviceCenterId);

        String sendRepairCompletedEmail(Long repairOrderId);

        RepairOrderVerificationResponse verifyRepairOrder(long repairOrderId, RepairOrderVerificationRequest request,
                        long userId, MultipartFile[] attachments) throws IOException;

        List<RepairHistoryResponse> getRecentRepairHistoryByVin(String vin);

        // Start a repair order (set start date and mark as IN_PROGRESS)
        void startRepairOrder(Long repairOrderId);
}