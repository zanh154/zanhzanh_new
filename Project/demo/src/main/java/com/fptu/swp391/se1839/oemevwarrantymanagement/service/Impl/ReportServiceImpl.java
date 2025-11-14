package com.fptu.swp391.se1839.oemevwarrantymanagement.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.RepairOrderDurationStatusResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.ServiceCenterPerformanceResponse;
import com.fptu.swp391.se1839.oemevwarrantymanagement.repository.RepairOrderRepository;
import com.fptu.swp391.se1839.oemevwarrantymanagement.service.ReportService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {

    RepairOrderRepository repairOrderRepository;

    @Override
    public List<ServiceCenterPerformanceResponse> getServiceCenterPerformance() {

        // Lấy dữ liệu raw từ DB
        List<Object[]> rawResults = repairOrderRepository.getCompletedOrderReport();

        // Map sang DTO sử dụng builder pattern
        return rawResults.stream()
                .map(r -> ServiceCenterPerformanceResponse.builder()
                        .scId((Long) r[0])
                        .scName((String) r[1])
                        .totalCompletedOrder((Long) r[2])
                        .build())
                .collect(Collectors.toList());
    }

    public RepairOrderDurationStatusResponse getCompletedOrderDurationStats() {

        long under24h = repairOrderRepository.countCompletedUnder24h();
        long under72h = repairOrderRepository.countCompletedUnder72h();
        long under168h = repairOrderRepository.countCompletedUnder168h();
        long over168h = repairOrderRepository.countCompletedOver168h();

        return RepairOrderDurationStatusResponse.builder()
                .under24h(under24h)
                .under72h(under72h)
                .under168h(under168h)
                .over168h(over168h)
                .build();
    }
}