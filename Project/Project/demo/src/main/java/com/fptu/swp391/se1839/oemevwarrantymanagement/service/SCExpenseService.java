package com.fptu.swp391.se1839.oemevwarrantymanagement.service;

import com.fptu.swp391.se1839.oemevwarrantymanagement.dto.response.SCExpenseResponse;

public interface SCExpenseService {
    double handleCalculateRevenueOfWeek(Long serviceCenterId);

    SCExpenseResponse handleWarrantyCostComparison(Long serviceCenterId);
}
